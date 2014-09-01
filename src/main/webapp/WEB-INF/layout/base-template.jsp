<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<c:if test="${empty root}">
	<c:set var="root" value="${pageContext.request.contextPath}" />
</c:if>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<script src="${root}/resources/js/moment.min.js"></script>
<script src="${root}/resources/js/jquery-1.8.0-min.js"></script>
<script src="${root}/resources/js/jquery-jtemplates.js"></script>
<title>TR Reports</title>
<link href="${root}/resources/css/bootstrap.css" rel="stylesheet">
<link href="${root}/resources/css/bootstrap-datepicker.css"
	rel="stylesheet">
<link href="${root}/resources/css/main.css" rel="stylesheet">
<!-- Custom styles for this template -->
<link href="${root}/resources/css/offcanvas.css" rel="stylesheet">
<link href="${root}/resources/css/select2.css" rel="stylesheet"/>
    <script src="${root}/resources/js/select2.js"></script>
<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->
</head>
<body>
<div id="wrap">
	<div class="navbar navbar-fixed-top navbar-inverse" role="navigation">
		<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".navbar-collapse">
					<span class="sr-only">Навигация</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href='<c:url value="/home.html" />'><img src="${root}/resources/img/logo.png" /></a>
			</div>
			<div class="collapse navbar-collapse">
				<ul class="nav navbar-nav navbar-right">
					
					<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown"><span class="glyphicon glyphicon-user"></span>
								<c:set var="username"><sec:authentication property="principal.name" /></c:set> 
								<c:if test="${empty username}">
									<c:set var="username"><sec:authentication property="principal.username" /></c:set> 
								</c:if>
								${username}
								<ul class="dropdown-menu">
								
									<li><a href='<c:url value="/users/edit.html" />'>Редактировать</a></li>
									
								
								<sec:authorize
									access="hasAnyRole('ROLE_ORGANIZER', 'ROLE_ADMIN')">
									<li role="presentation" class="divider"></li>
									<c:set var="region"><sec:authentication property="principal.organizer.region" /></c:set>
									<c:if test="${region }">
										<li><a role="menuitem" tabindex="-1" href="#">Регион: <sec:authentication property="principal.organizer.region.name" /></a></li>
										<li><a role="menuitem" tabindex="-1" href="#">Организатор: <sec:authentication property="principal.organizer.name" /></a></li>
										<li><a role="menuitem" tabindex="-1" href="#">ИНН: <sec:authentication property="principal.organizer.inn" /></a></li>
									</c:if>
								</sec:authorize>

							</ul>
								
					</a>	</li>
					<sec:authorize access="hasAnyRole('ROLE_ORGANIZER', 'ROLE_ADMIN')">
						<li class="dropdown"><a href="#" class="dropdown-toggle"
							data-toggle="dropdown"><span class="glyphicon glyphicon-cog"></span>
								Настройки<b class="caret"></b></a>
							<ul class="dropdown-menu">
								<sec:authorize access="hasAnyRole('ROLE_ADMIN')">
									<li><a href="<c:url value='/settings.html' />">Организаторы</a></li>
								</sec:authorize>
								<sec:authorize
									access="hasAnyRole('ROLE_ORGANIZER', 'ROLE_ADMIN')">
									<li><a href="<c:url value='/users/list.html' />">Пользователи</a></li>
								</sec:authorize>

							</ul></li>

					</sec:authorize>

					<li><a href='<c:url value="/j_spring_security_logout" />'><span
							class="glyphicon glyphicon-share"></span> Выйти</a></li>
				</ul>
			</div>
			<!-- /.nav-collapse -->
		</div>
		<!-- /.container -->
	</div>
	<!-- /.navbar -->

	<div class="container">
		<div class="row row-offcanvas row-offcanvas-right">

			<div class="col-xs-12 col-sm-9">
				<p class="pull-right visible-xs">
					<button type="button" class="btn btn-primary btn-xs"
						data-toggle="offcanvas">Отчеты</button>
				</p>


				<tiles:insertAttribute name="content" />
			</div>
			<div class="col-xs-6 col-sm-3 sidebar-offcanvas" id="sidebar"	role="navigation">
				<div class="list-group">
					
					<sec:authorize access="hasAnyRole('ROLE_ORGANIZER','ROLE_USER', 'ROLE_ADMIN')">
						<a class="list-group-item <c:if test="${page eq 'daily' }">active</c:if>" href="<c:url value='/home.html' />">Динамика продаж</a>
						<a class="list-group-item  <c:if test="${page eq 'organizer' }">active</c:if>"" href="<c:url value='/reports/organizers.html' />">Отчет
											организатора по событиям</a>
						<a class="list-group-item  <c:if test="${page eq 'etickets' }">active</c:if>"" href="<c:url value='/reports/etickets.html' />">Отчет
											продажи e-билетов</a>
					</sec:authorize>
					<sec:authorize access="hasAnyRole('ROLE_SKA', 'ROLE_ADMIN')">
						<a href="<c:url value='/reports/sibur/main.html' />"
							class="list-group-item  <c:if test="${page eq 'spartak' }">active</c:if>"">Отчет по кассам</a>
					</sec:authorize>
					

				</div>
				
				
			</div>
			<!--/span-->
		</div>
	</div>
</div>

	<footer class="footer" >
    <div class="container" style="margin:0 auto;">
        <div class="row">&copy; <a href="http://foggylab.ru/">foggylab</a>,  2014
            
            
        </div>
    </div>
</footer>


	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	
	<script src="${root}/resources/js/json2.js"></script>
	<script src="${root}/resources/js/bootstrap.min.js"></script>
	<script src="${root}/resources/js/bootstrap-datepicker.js"></script>
	<script src="${root}/resources/js/offcanvas.js"></script>
	<script src="${root}/resources/js/highcharts.js"></script>
	<script src="${root}/resources/js/modules/exporting.js"></script>
	<script type="text/javascript"
		src="${root}/resources/js/bootstrap-datepicker.ru.js" charset="UTF-8"></script>
	<script type="text/javascript">
		$('.datepicker').datepicker({
			format : 'dd.mm.yyyy',
			language : 'ru'
		});
		
	</script>
</body>
</html>