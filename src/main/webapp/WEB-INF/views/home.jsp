<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set value="${fn:length(reports)-1}" var="size" />
  <h2>Продажи <small>за 30 дней начиная с <fmt:formatDate pattern="dd.MM.yyy" 
            value="${reports[size].reportDate}" /></small></h2>


<form method="get" id="form">
<div class="panel panel-default">
<!--   <div class="panel-heading">
    <h3 class="panel-title">Фильтр</h3>
  </div> -->
  <div class="panel-body">

Событие
<select id="event" style="width:70%" name="id">

<option value=""  >Все события</option>
<c:set value="0" var="actionId" />
<c:set value="0" var="printOptGroup" />

	<c:forEach items="${events }" var="ev" varStatus="status">
	<c:if test="${status.first}"><optgroup label="${ev.name }"></c:if>
	<c:if test="${actionId != ev.actionId}"><c:set value="1" var="printOptGroup" /></c:if>
	<c:set var="sel" value=""/>
	<c:if test="${printOptGroup == 1}"></optgroup><optgroup label="${ev.name }">
	
	</c:if>
	<c:if test="${eventId == ev.externalId }"><c:set var="sel" value="selected"/> </c:if>
    <option value="${ev.externalId }" ${sel } >${ev.name } <date><fmt:formatDate pattern="dd.MM.yyyy HH:mm"  value="${ev.startDate}" /></date></option>
	
	<c:set value="0" var="printOptGroup" />
	<c:set value="${ev.actionId }" var="actionId" />
	<c:if test="${status.last}"></optgroup></c:if>
	</c:forEach>
    </select>
      </div>
</div>
    </form>	
<div class="alert alert-success"><%-- <small>За <fmt:formatDate pattern="dd.MM.yyy" 
            value="${reports[size].reportDate}" /></small> --%>
<button type="button" class="btn btn-link"> Передано <span class="badge pull-bottom">
<fmt:formatNumber value="${reports[size].summary.transferred}" pattern="#,###.##" var="pat" /> 
${fn:replace(pat, ",", " ")}</span>
</button>
<button type="button" class="btn btn-link"> Возвращено 
<span class="badge pull-bottom"><fmt:formatNumber value="${reports[size].summary.returned}" pattern="#,###.##" var="pat" /> 
${fn:replace(pat, ",", " ")}</span></button>

<button type="button" class="btn btn-link"> Забронировано 
<span class="badge pull-bottom"><fmt:formatNumber value="${reports[size].summary.reserved}" pattern="#,###.##" var="pat" /> 
${fn:replace(pat, ",", " ")}</span></button>

<button type="button" class="btn btn-link ">  В продаже 
<span class="badge pull-bottom"><fmt:formatNumber value="${reports[size].summary.free}" pattern="#,###.##" var="pat" /> 
${fn:replace(pat, ",", " ")}</span></button>
<button type="button" class="btn btn-link"> Продано	 <span class="badge pull-bottom">
<fmt:formatNumber value="${reports[size].summary.electronicSoldTickets+reports[size].summary.soldTickets}" pattern="#,###.##" var="pat" /> 
${fn:replace(pat, ",", " ")}</span>
</button>
</div>

<c:url value="/home.html" var="link"></c:url>


