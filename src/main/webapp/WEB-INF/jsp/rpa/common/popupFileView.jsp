<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="egovframework.com.cmm.LoginVO" %>
<%
	LoginVO user = (LoginVO)request.getSession().getAttribute("LoginVO");
	String userId = user.getId();


    SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMddHHmmss");
    String today = formatter1.format(new Date());
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link href="<c:url value='/'/>css/common.css?<%=today%>" rel="stylesheet" type="text/css" >

<script type="text/javascript" src="<c:url value='/js/EgovBBSMng.js' />"></script>
<script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.js" ></script>

<script src="http://code.jquery.com/jquery-1.10.2.js"></script>
<script src="http://code.jquery.com/ui/1.11.2/jquery-ui.js"></script>
<script type="text/javascript" src="<c:url value='/js/egovframework/com/cmm/fms/EgovMultiFile.js'/>" ></script>
<script src="https://malsup.github.io/jquery.form.js"></script>
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">

<link type="text/css" rel="stylesheet" href="<c:url value='/css/egovframework/com/com.css?<%=today%>' />">
</head>
<body>

<div id="search_field" style="width:450px"> 
	<div id="search_field_loc"><h2><strong>통관정보 파일 정보</strong></h2></div>
</div>
<form id="frm" name="frm" enctype="multipart/form-data">
	<input type="hidden" name="atchFileId" id="atchFileId" />
	<input type="hidden" name="pathurl" id="pathurl" />
	<input type="hidden" name="searchBlNo" value='<c:out value="${searchVO.searchBlNo}"/>' />
    <input type="hidden" name="searchFileCn" value='<c:out value="${searchVO.searchFileCn}"/>' />
    <input type="hidden" name="searchCompany" value='<c:out value="${searchVO.searchCompany}"/>' />
	<table width="850px" id="tbllist"  style="border-spacing: 0 10px;border-bottom: 10px solid #fff;">
		<colgroup>
			<col width="350px">
			<col width="450px">  
			<col width="40px">
		</colgroup>
		<c:forEach var="result" items="${resultList}" varStatus="status">
		<tr>
		    <td style="text-align:left">
		    	<a name="file" style="cursor:pointer" content_id="${result.atchFileId}" ext_id="${result.fileExtsn}"  path_id="${result.fileStreCours}" ><c:out value="${result.orignlFileNm}" default=""/></a>
			</td>
			<td style="text-align:left">
		    	<c:out value="${result.fileGubunNm}" default=""/>
			</td>
			<td style="text-align:center">
			<c:if test="${searchVO.searchCompany == 'Y'}">
			<% if (userId.equals("admin") || userId.equals("99000303"))  {%>
				  <a name="filedel"  content_id="${result.atchFileId}" ext_id="${result.fileStreCours}${result.streFileNm}" ><img src="/images/del.png"  height="25" border="0" style="cursor: pointer" /></a>	 
		    <% } %>
			</c:if>
			</td>
		</tr>
		<c:if test="${fn:length(resultList) == 0}">
		  <tr>
		    <td nowrap colspan="3"><spring:message code="common.nodata.msg" /></td>  
		  </tr>		 
		</c:if>
		</c:forEach>	
	</table>
</form>	    
<table width="100%" id="ifrm" style="margin-left: auto; margin-right: auto;border-collapse: separate;  border-spacing: 0 10px;border-bottom: 10px solid #fff;">
	<tr>
	    <td style="text-align:left">
	    <iframe id='my_frame' width="98%"></iframe>
		</td>
	</tr>
</table>  
 <form name="myForm" id="myForm" method="post">
    <input type="hidden" name="searchBgnDe" id="searchBgnDe" value='<c:out value="${searchVO.searchBgnDe}"/>' />
    <input type="hidden" name="searchEndDe" id="searchEndDe" value='<c:out value="${searchVO.searchEndDe}"/>' />
    <input type="hidden" name="searchGubun" id="searchGubun" value='<c:out value="${searchVO.searchGubun}"/>' />
    <input type="hidden" name="searchConfirm" id="searchConfirm" value='<c:out value="${searchVO.searchConfirm}"/>' />
    <input type="hidden" name="searchCom" id="searchCom" value='<c:out value="${searchVO.searchCom}"/>' />
</form>
<script>
$(function() {
	var tableHeight = $("#tbllist").height(); 

	$('#ifrm').css('height', $(window).height() - tableHeight - 50 );
	
	$('#my_frame').css('height', $(window).height() - tableHeight - 100 );
	
	$("a[name='file']").click(function(){
		var ext_id = $(this).attr("ext_id").toLowerCase();
		var content_id = $(this).attr("content_id");
		var path_id = $(this).attr("path_id");
		//$("#pathurl").val(pathurl);
		if (ext_id == "pdf"){
			$('#my_frame').attr('src', "/rpa/cmm/pdfFileView.do?searchAtchFileId="+content_id);
		}
		else if ((ext_id == "png") ||(ext_id == "gif") || (ext_id == "jpg") || (ext_id == "jpeg")){
			$('#my_frame').attr('src', "/rpa/cmm/imgView.do?atchFileId="+content_id);
		}
		else{
			$('#my_frame').attr('src', "/rpa/cmm/display.do?searchAtchFileId="+content_id);
		}

    });
	
	$("a[name='filedel']").click(function(){
		var atchFileId = $(this).attr("content_id");
		var filepath = $(this).attr("ext_id");

		$('#atchFileId').val(atchFileId);
		$('#pathurl').val(filepath);
		
		fn_fileDel();
    });
	
	$(window).bind('resize', function(e)
	{
		$('#ifrm').css('height', $(window).height() - tableHeight - 50 );
		
		$('#my_frame').css('height', $(window).height() - tableHeight - 100 );
	});
	
});


function fn_fileDel(atchFileId) {
	
    if (window.confirm("파일을 삭제하시겠습니까?")) {
        $("#frm").ajaxForm({
            type: 'POST',
            url: '<c:url value='/rpa/cmm/deleteFileSap.do'/>',
            dataType: "json",
            enctype: "multipart/form-data",
            contentType: false,
            processData: false,
            timeout: 50000,
            success: function(result) {

            	if (result.status == 0) {
                    alert(result.msg);
                    if (result.filecnt == 0) {
                    	document.myForm.method = "post";
                    	document.myForm.action = "/rpa/cmm/selectExtraSubList.do";
                    	document.myForm.target = opener.window.name; 
                    	document.myForm.submit();
                    	self.close();
                    }
                    else{
                    	alert(result.filecnt);
                    	document.frm.action = "/rpa/cmm/selectFileView.do";
                		document.frm.method = "post";
                        document.frm.submit();
                        document.myForm.method = "post";
                    	document.myForm.action = "/rpa/cmm/selectExtraSubList.do";
                    	document.myForm.target = opener.window.name; 
                    	document.myForm.submit();
                    }
                    
                }
                else if (result.status == 1) {
                    alert(result.msg);
                }
                    
            },
            error: function(data, status, err) {
            	
                alert("서버가 응답하지 않습니다." + "\n" + "다시 시도해주시기 바랍니다." + "\n"
                    + "code: " + data.status + "\n"
                    + "message :" + data.responseText + "\n"
                    + "message1 : " + status + "\n"
                    + "error: " + err);
            }
        }).submit();
    }


}

function open_in_frame(url) {
	$('#my_frame').attr('src', url);
}
</script>
</body>

</html> 
