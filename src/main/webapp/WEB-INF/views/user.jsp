<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="caption" value="Editing" />
	<c:if test="${empty userForm.id}">
		<c:set var="caption" value="Creating" />
	</c:if>
<div id="formsContent">
	
	<h2>${caption} user</h2>

	<form:form id="form" method="post" modelAttribute="userForm"
		class="form-horizontal" role="form">
		<c:if test="${not empty message}">
			<div class="alert alert-success">The changes have been saved</div>
		</c:if>
		<s:bind path="*">
			<c:if test="${status.error}">
				<div id="message" class="alert alert-danger">Error saving user changes</div>
			</c:if>
		</s:bind>
		<form:hidden path="id" />
		<fieldset>
			<legend>Information</legend>
			<div class="form-group error">
				<c:set var="loginReadOnly" value="true" />
				<c:if test="${empty userForm.id}">
					<c:set var="loginReadOnly" value="false" />
				</c:if>
				<form:label class="col-lg-2 control-label" path="username"> 
						Login
				</form:label>
				<div class="col-lg-6">
					<form:input readonly="${loginReadOnly}" path="username" class="form-control"  type="text" autocorrect="off" autocapitalize="off" 
						placeholder="username" />
				</div>

				<form:errors path="username" cssClass="alert alert-danger"
					element="div" />

			</div>
			<div class="form-group warning">
				<form:label class="col-lg-2 control-label" path="password">
		  				Password 
				</form:label>
				<div class="col-lg-6">
					<form:password class="form-control" path="password" />
				</div>
				<form:errors path="password" cssClass="alert alert-danger"
					element="div" />
			</div>
		</fieldset>
		<sec:authorize access="hasAnyRole('ROLE_ADMIN')">
		<fieldset>
			<legend>Roles</legend>
			<div class="form-group">
				<div class="col-lg-6">
					<form:select class="form-control" path="role">
						<form:option value="ROLE_USER">User</form:option>
						<form:option value="ROLE_ADMIN">Administrator</form:option>
					</form:select>
				</div>
			</div>
		</fieldset>
		</sec:authorize>

		<fieldset><br />
		<p>
			<button type="submit" class="btn btn-primary">Save</button>
		</p>
		</fieldset>
	</form:form>
</div>
