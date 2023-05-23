<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<head>    
<link href="bootstrap/bootstrap.min.css" rel="stylesheet">
<link href="https://fonts.googleapis.com/css?family=Playfair&#43;Display:700,900&amp;display=swap" rel="stylesheet">
<link href="bootstrap/blog.css" rel="stylesheet">
</head>
<body>

    <div class="dropdown position-fixed bottom-0 end-0 mb-3 me-3 bd-mode-toggle">
<%
if(request.getAttribute("stock") == null){
%>
      <button class="btn btn-danger py-2 d-flex align-items-center"
              id="bd-theme"
              type="button"
              aria-expanded="false"
              data-bs-toggle="dropdown"
              aria-label="Toggle theme (auto)">
        Stock Load Failed !
      </button>
<%
} else { 
%>
      <button class="btn btn-bd-primary py-2 d-flex align-items-center"
              id="bd-theme"
              type="button"
              aria-expanded="false"
              data-bs-toggle="dropdown"
              aria-label="Toggle theme (auto)">
        Stock Data 보러가기
      </button>
<%
}
%>
    </div>
    
<div class="container">
  <header class="blog-header lh-1 py-3">
    <div class="row flex-nowrap justify-content-between align-items-center">
<%
if(request.getAttribute("accounts") == null){
%>
      <div class="col-4 pt-1">
      	<strong class=text-danger>Weather Load Failed !</strong>
      </div>
<%
} else { 
%>
      <div class="col-4 pt-1">
        <a class="link-secondary" href="#">OPENMARU 날씨가 맑습니다 !</a>
      </div>
<%
}
%>
      <div class="col-4 text-center">
        <a class="blog-header-logo text-body-emphasis" href="#">Portal</a>
      </div>
<%
if(request.getAttribute("accounts") == null){
%>
      <div class="col-4 d-flex justify-content-end align-items-center">
        <a class="btn btn-sm btn-outline-secondary text-danger" href="#">Accounts Load Failed !</a>
      </div>
<%
} else { 
%>
      <div class="col-4 d-flex justify-content-end align-items-center">
        <a class="btn btn-sm btn-outline-secondary" href="#">안병욱님 로그인 성공 !</a>
      </div>
<%
}
%>
    </div>
  </header>
</div>

<main class="container">
  <div class="p-4 p-md-5 mb-4 rounded text-bg-dark">
    <div class="col-md-6 px-0">
<%
if(request.getAttribute("news") == null){
%>
	  <h1 class=text-danger>News Load Failed !</h1>
      <p class=text-danger>본문 불러오기 실패.</p>
<%
} else { 
%>
	  <h1>OPENMARU NEWS</h1>
      <p>오픈나루는 기업의 IT 시스템을 오픈소스 기반으로 구축하고자 할 때 필요한 킬러 서비스와 솔루션을 제공하는 전문기술 기업입니다. 고객의 IT 환경을 안심하고 오픈소스 환경으로 전환하고 안정적인 운영할 수 있도록 도와 드리는 제품과 서비스 라인업을 제공합니다. 오픈나루는 차세대 IT의 중심인 오픈소스 소프트웨어 기반으로 고객이 새로운 비즈니스 가치를 창조할 수 있도록 돕는 파트너가 되겠습니다.</p>
      <p class="lead mb-0"><a href="https://www.openmaru.io/" class="text-white fw-bold">OPENMARU 더 알아보기...</a></p>
<%
}
%>
    </div>
  </div>
  <div class="row mb-2">
    <div class="col-md-6">
      <div class="row g-0 border rounded overflow-hidden flex-md-row mb-4 shadow-sm h-md-250 position-relative">
        <div class="col p-4 d-flex flex-column position-static">
<%
if(request.getAttribute("review-v1") == null){
%>
          <strong class="d-inline-block mb-2 text-danger">Failed Review</strong>
          <strong class=text-danger><h3>Review Load Failed !</h3></strong>
          <div class="mb-1 text-body-secondary text-danger">Unknown</div>
          <p class="card-text mb-auto text-danger">본문 불러오기 실패.</p>
<%
} else { 
%>
          <strong class="d-inline-block mb-2 text-primary">First Review</strong>
          <h3 class="mb-0">OPENMARU APM</h3>
          <div class="mb-1 text-body-secondary">Nov 12</div>
          <p class="card-text mb-auto">OPENMARU APM 시스템 장애 해결과 성능 관리 어플리케이션 완벽 운영!</p>
          <a href="https://www.openmaru.io/openmaru-cloud-apm-2/application-monitoring/" class="stretched-link">자세히...</a>
<%
}
%>
        </div>
      </div>
    </div>
    <div class="col-md-6">
      <div class="row g-0 border rounded overflow-hidden flex-md-row mb-4 shadow-sm h-md-250 position-relative">
        <div class="col p-4 d-flex flex-column position-static">
<%
if(request.getAttribute("review-v2") == null){
%>
          <strong class="d-inline-block mb-2 text-danger">Failed Review</strong>
          <strong class=text-danger><h3>Review Load Failed !</h3></strong>
          <div class="mb-1 text-body-secondary">Unknown</div>
          <p class="card-text mb-auto text-danger">본문 불러오기 실패.</p>
<%
} else { 
%>
          <strong class="d-inline-block mb-2 text-success">Second Review</strong>
          <h3 class="mb-0">OPENMARU Cluster</h3>
          <div class="mb-1 text-body-secondary">Nov 11</div>
          <p class="mb-auto">오픈소스 및 이기종 WAS를 위한 세션 클러스터링 솔루션!</p>
          <a href="http://www.opennaru.com/openmaru/openmaru-cluster/" class="stretched-link">자세히...</a>
<%
}
%>
        </div>
      </div>
    </div>
  </div>
</body>
</html>
