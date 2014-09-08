package ru.nilebox.collabedit.tests;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.nilebox.collabedit.operations.DeleteOperation;
import ru.nilebox.collabedit.operations.InsertOperation;
import ru.nilebox.collabedit.operations.RetainOperation;
import ru.nilebox.collabedit.transform.service.DocumentChangeRequest;
import ru.nilebox.collabedit.transform.service.OperationContainer;

/**
 *
 * @author nile
 */
public class SerializationTest {

	@Test
	public void serializeRequest() throws IOException {
		DocumentChangeRequest request1 = new DocumentChangeRequest();
		request1.setRequestId("1bbfdbd6-dfd0-4932-bf72-7972e173be7e");
		request1.setClientId("c689c105-3d57-4318-91fa-864ee4d0c78b");
		request1.setDocumentId(Long.MAX_VALUE);
		request1.setBaseDocumentVersion(123);
		
		List<OperationContainer> operations1 = new ArrayList<OperationContainer>();
		operations1.add(OperationContainer.fromOperation(new RetainOperation(4)));
		operations1.add(OperationContainer.fromOperation(new InsertOperation("text")));
		operations1.add(OperationContainer.fromOperation(new DeleteOperation(3)));
		
		request1.setOperations(operations1);
		
		StringWriter sw = new StringWriter();   // serialize
		ObjectMapper mapper = new ObjectMapper();
		MappingJsonFactory jsonFactory = new MappingJsonFactory();
		JsonGenerator jsonGenerator = jsonFactory.createGenerator(sw);
		mapper.writeValue(jsonGenerator, request1);
		sw.close();
		
		String output = sw.getBuffer().toString();
		
		// Check that output is not changed
		String expected = "{\"requestId\":\"1bbfdbd6-dfd0-4932-bf72-7972e173be7e\","
				+ "\"clientId\":\"c689c105-3d57-4318-91fa-864ee4d0c78b\","
				+ "\"documentId\":9223372036854775807,\"baseDocumentVersion\":123,"
				+ "\"operations\":[{\"retainOp\":{\"length\":4}},"
				+ "{\"insertOp\":{\"text\":\"text\"}},{\"deleteOp\":{\"length\":3}}]}";
		System.out.println(output);
		Assert.assertEquals(output, expected);
		
		// Check that properties have correct values after serialization/deserialization
		StringReader sr = new StringReader(output);
		DocumentChangeRequest request2 = mapper.readValue(sr, DocumentChangeRequest.class);
		
		Assert.assertEquals(request2.getClientId(), request1.getClientId());
		Assert.assertEquals(request2.getDocumentId(), request1.getDocumentId());
		Assert.assertEquals(request2.getBaseDocumentVersion(), request1.getBaseDocumentVersion());
		Assert.assertEquals(request2.getRequestId(), request1.getRequestId());
		Assert.assertNotNull(request2.getOperations());
		Assert.assertEquals(request2.getOperations().size(), request1.getOperations().size());

		OperationContainer oc2 = request2.getOperations().get(0);
		Assert.assertNotNull(oc2);
		Assert.assertNull(oc2.getInsertOp());
		Assert.assertNull(oc2.getDeleteOp());
		Assert.assertNotNull(oc2.getRetainOp());
		
		for (int i=0; i < request2.getOperations().size(); i++) {
			Assert.assertEquals(request2.getOperations().get(i), request1.getOperations().get(i));
		}

	}
}
