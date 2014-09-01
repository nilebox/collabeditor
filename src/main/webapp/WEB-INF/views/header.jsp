<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Отчет по кассе</title>
<link href="resources/css/bootstrap.css" rel="stylesheet">
<link href="resources/css/bootstrap-datepicker.css" rel="stylesheet">
<link href="resources/css/main.css" rel="stylesheet">
<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->
</head>
<body>
	<div id="wrap">
		<div class="container">
			<div class="navbar" role="navigation">
				<div class="navbar-header">
					<img src="resources/img/logo_kassir.png" >
				</div>
				<div class="navbar-collapse collapse">
					<ul class="nav navbar-nav navbar-right">

						<li><div class="user">
							<span class="glyphicon glyphicon-user"></span> <span class="text-info">${username} </span>
						</div></li>
						<li><a href='<c:url value="/j_spring_security_logout" />'>Выйти</a></li>
					</ul>
				</div>
			</div>
			<div class="row">
				<div class="col-md-3 sidebar-offcanvas" id="sidebar" role="navigation">
					<div class="list-group">
						<a class="list-group-item active">
						<span class="glyphicon glyphicon-list-alt">&nbsp;</span>Отчеты</a> 
						<a href='<c:url value="/main" />' class="list-group-item">Отчет по
							продажам организатора</a>
					</div>
				</div>
				<div class="col-md-9">
					<form method="post" class="form-horizontal" action='<c:url value="/main" />'>
						<p class="lead text-primary">Получить отчет</p>
						<div class="row">
							<div class="col-xs-2">
								<input type="text" class="datepicker form-control" name="startDate" value="${datePicker.startDate}">
							</div>
							<div class="col-xs-2">
								<input type="text" class="datepicker form-control" name="endDate" value="${datePicker.endDate}">
							</div>
							<div class="col-xs-8">
								<button class="btn btn-primary" type="submit">Получить</button>
							</div>
						</div>
						<c:if test="${not empty parserError}">
							<div class="row">
								<div class="col-xs-12">
									<p class="text-danger">
										<br />Неправильный диапазон дат
									</p>
								</div>
							</div>
						</c:if>
						<c:if test="${not empty remoteError}">
							<div class="row">
								<div class="col-xs-12">
									<p class="text-danger">
										<br />Ошибка сервера, смотрите серверный лог-файл для выяснения деталей.
									</p>
								</div>
							</div>
						</c:if>
						<c:if test="${pageContext.request.method == 'POST' and empty report and empty remoteError and empty parserError}">
							<div class="row">
								<div class="col-xs-12">
									<p class="text-info">
										<br />Нет данных за <span class="text-muted">${datePicker.startDate} - ${datePicker.endDate}</span>.<br /> Попробуйте другие даты.
									</p>
								</div>
							</div>
						</c:if>
					</form>
				<c:if test="${not empty report}">
					<c:import url="main-report.jsp" />
				</c:if>

				</div>
			</div>

		</div>
	</div>

	<div id="footer">
		<div class="container">
			<div class="row">
					<div class="col-md-12">
						<p class="text-right developed_by">Разработка:  <a href="//foggylab.ru"><img class="logo_foggy" src="resources/img/logo_foggy.png" alt="FoggyLab" title="FoggyLab"></a></p>
					</div>
				</div>	
		</div>
	</div>

	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script src="resources/js/jquery-1.8.0-min.js"></script>
	<script src="resources/js/json2.js"></script>
	<script src="resources/js/bootstrap.min.js"></script>
	<script src="resources/js/bootstrap-datepicker.js"></script>
	<script type="text/javascript" src="resources/js/bootstrap-datepicker.ru.js" charset="UTF-8"></script>
	<script type="text/javascript">
		$('.datepicker').datepicker({
			format : 'dd.mm.yyyy',
			language : 'ru'
		});
	</script>
</body>
</html>