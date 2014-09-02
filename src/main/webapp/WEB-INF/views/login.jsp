<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Login page</title>

<!-- Bootstrap core CSS -->
<link href="resources/css/bootstrap.css" rel="stylesheet">
<!-- Custom styles for this template -->
<link href="resources/css/login.css" rel="stylesheet">
<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->
</head>
<body>
	
	<div class="container-fluid">
		<div class="row">
			<div class="col-md-4 col-xs-2" placeholder=".col-xs-4"></div>
			<div class="col-md-4 col-xs-6" placeholder=".col-xs-4">
				<h2 class="text-primary lead">Please sign in</h2>
			</div>
			<div class="col-md-4 col-xs-2" placeholder=".col-xs-4"></div>
		</div>
		<c:choose>
			<c:when test="${empty error}">
				<form class="form-horizontal" action="<c:url value='j_spring_security_check' />" method='POST'>
					<div class="row">
						<div class="col-md-4 col-xs-2" placeholder=".col-xs-4"></div>
						<div class="col-md-4 col-xs-6" placeholder=".col-xs-4">
							<p>
								<input type="text" autocorrect="off" autocapitalize="off" class="form-control" placeholder="Login"  name='j_username'>
							</p>
						</div>
						<div class="col-md-4 col-xs-2" placeholder=".col-xs-4"></div>
					</div>
					<div class="row">
						<div class="col-md-4 col-xs-2" placeholder=".col-xs-4"></div>
						<div class="col-md-4 col-xs-6" placeholder=".col-xs-4">
							<p>
								<input type="password" class="form-control" placeholder="Password" required name='j_password'>
							</p>
						</div>
						<div class="col-md-4 col-xs-2" placeholder=".col-xs-4"></div>
					</div>
					<div class="row">
						<div class="col-md-4 col-xs-2" placeholder=".col-xs-4"></div>
						<div class="col-md-4 col-xs-10" placeholder=".col-xs-4">
							<p>
								<button class="btn btn-primary" type="submit">Sign in</button>
							</p>
						</div>
						<div class="col-md-4 col-xs-2" placeholder=".col-xs-4"></div>
					</div>
				</form>
			</c:when>
			<c:otherwise>
				<div class="row">
					<div class="col-xs-4" placeholder=".col-xs-4"></div>
					<div class="col-xs-4" placeholder=".col-xs-4">
						<p class="text-warning lead">Sign in error:</p>
					</div>
					<div class="col-xs-4" placeholder=".col-xs-4"></div>
				</div>
				<div class="row">
					<div class="col-xs-4" placeholder=".col-xs-4"></div>
					<div class="col-xs-4" placeholder=".col-xs-4">
						<p class="text-danger">${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}</p>
					</div>
					<div class="col-xs-4" placeholder=".col-xs-4"></div>
				</div>
				<div class="row">
					<div class="col-xs-4" placeholder=".col-xs-4"></div>
					<div class="col-xs-4" placeholder=".col-xs-4">
						<a class="btn btn-primary" href="<c:url value='login.html' />" role="button">Try again</a>
					</div>
					<div class="col-xs-4" placeholder=".col-xs-4"></div>
				</div>
			</c:otherwise>
		</c:choose>
	</div>
</body>
</html>
