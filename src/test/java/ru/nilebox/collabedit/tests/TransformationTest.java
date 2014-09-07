package ru.nilebox.collabedit.tests;

import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.nilebox.collabedit.operations.DeleteOperation;
import ru.nilebox.collabedit.transform.ContentManager;
import ru.nilebox.collabedit.transform.DocumentTransformer;
import ru.nilebox.collabedit.operations.InsertOperation;
import ru.nilebox.collabedit.operations.Operation;
import ru.nilebox.collabedit.operations.OperationBatch;
import ru.nilebox.collabedit.operations.RetainOperation;
import ru.nilebox.collabedit.transform.TransformationException;
import ru.nilebox.collabedit.util.Pair;

/**
 *
 * @author nile
 */
public class TransformationTest {
	
	private static RetainOperation retain(int length) {
		return new RetainOperation(length);
	}
	
	private static InsertOperation insert(String text) {
		return new InsertOperation(text);
	}
	
	private static DeleteOperation delete(int length) {
		return new DeleteOperation(length);
	}
	
	public static OperationBatch operationBatch(Operation... operations) {
		OperationBatch oc = new OperationBatch();
		for (Operation op : operations) {
			oc.add(op);
		}
		return oc;
	}	
	
	private void checkTransform(String original, OperationBatch first, OperationBatch second, String expected) throws TransformationException {
		Pair<OperationBatch> transform = DocumentTransformer.transformBatches(first, second);
		
		ContentManager cm1 = new ContentManager(original);
		cm1.applyOperations(first);
		cm1.applyOperations(transform.getSecond());
		
		ContentManager cm2 = new ContentManager(original);
		cm2.applyOperations(second);
		cm2.applyOperations(transform.getFirst());
		
		Assert.assertEquals(cm1.getContent(), cm2.getContent(), "result is different for different order of operations");
		Assert.assertEquals(cm1.getContent(), expected, "invalid operation result");
	}

	@Test
	public void simpleTransform() throws TransformationException {
		String original = "A B C D E";
		
		OperationBatch first = operationBatch(insert("a "), retain(2), insert("c "), retain(2), delete(4));
		OperationBatch second = operationBatch(insert("b "), retain(6), insert("d "), retain(2), delete(2));
		
		Pair<OperationBatch> transform = DocumentTransformer.transformBatches(first, second);		
		
		ContentManager cm1 = new ContentManager(original);
		cm1.applyOperations(first);
		Assert.assertEquals(cm1.getContent(), "a A c B E");
		cm1.applyOperations(transform.getSecond());
		
		ContentManager cm2 = new ContentManager(original);
		cm2.applyOperations(second);
		Assert.assertEquals(cm2.getContent(), "b A B C d D ");
		cm2.applyOperations(transform.getFirst());
		
		Assert.assertEquals(cm1.getContent(), cm2.getContent());
		Assert.assertEquals(cm1.getContent(), "a b A c B d ");
		
		// However not needed for this test case, check method's consistency
		checkTransform(original, first, second, "a b A c B d ");
	}
	
	@Test
	public void splitDeleteTransform() throws TransformationException {
		String original = "ABCDEF";
		
		OperationBatch first = operationBatch(retain(3), insert("x"));
		OperationBatch second = operationBatch(retain(2), delete(3));
		
		checkTransform(original, first, second, "ABxF");
	}
	
	@Test
	public void simultaneousDeleteTransform() throws TransformationException {
		String original = "ABCDEF";
		
		OperationBatch first = operationBatch(retain(2), delete(3));
		OperationBatch second = operationBatch(retain(2), delete(2));
		
		checkTransform(original, first, second, "ABF");
	}	
	
	@Test
	public void chainTransform() throws TransformationException {
		String original = "ABCDEF";
		OperationBatch first = operationBatch(retain(3), insert("x"));
		OperationBatch second = operationBatch(retain(3), insert("y"));
		OperationBatch third = operationBatch(retain(3), insert("z"));
		OperationBatch fourth = operationBatch(retain(2), delete(3));
		
		second = DocumentTransformer.transformBatches(first, second).getSecond();
		
		third = DocumentTransformer.transformBatches(first, third).getSecond();
		third = DocumentTransformer.transformBatches(second, third).getSecond();
		
		fourth = DocumentTransformer.transformBatches(first, fourth).getSecond();
		fourth = DocumentTransformer.transformBatches(second, fourth).getSecond();
		fourth = DocumentTransformer.transformBatches(third, fourth).getSecond();
		
		ContentManager cm = new ContentManager(original);
		cm.applyOperations(first);
		cm.applyOperations(second);
		cm.applyOperations(third);
		cm.applyOperations(fourth);

		Assert.assertEquals(cm.getContent(), "ABxyzF", "invalid operation result");
	}
	
}
