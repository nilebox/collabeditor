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
		
		ContentManager dp1 = new ContentManager(original);
		dp1.applyOperations(first);
		dp1.applyOperations(transform.getSecond());
		
		ContentManager dp2 = new ContentManager(original);
		dp2.applyOperations(second);
		dp2.applyOperations(transform.getFirst());
		
		Assert.assertEquals(dp1.getContent(), dp2.getContent(), "result is different for different order of operations");
		Assert.assertEquals(dp1.getContent(), expected, "invalid operation result");
	}

	@Test
	public void simpleTransform() throws TransformationException {
		String original = "A B C D E";
		
		OperationBatch first = operationBatch(insert("a "), retain(2), insert("c "), retain(2), delete(4));
		OperationBatch second = operationBatch(insert("b "), retain(6), insert("d "), retain(2), delete(2));
		
		Pair<OperationBatch> transform = DocumentTransformer.transformBatches(first, second);		
		
		ContentManager dp1 = new ContentManager(original);
		dp1.applyOperations(first);
		Assert.assertEquals(dp1.getContent(), "a A c B E");
		dp1.applyOperations(transform.getSecond());
		
		ContentManager dp2 = new ContentManager(original);
		dp2.applyOperations(second);
		Assert.assertEquals(dp2.getContent(), "b A B C d D ");
		dp2.applyOperations(transform.getFirst());
		
		Assert.assertEquals(dp1.getContent(), dp2.getContent());
		Assert.assertEquals(dp1.getContent(), "a b A c B d ");
		
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
	
}
