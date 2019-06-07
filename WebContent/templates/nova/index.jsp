<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html>
<html>
<head>
   <meta charset="UTF-8"/>
   <base href="${path}"/>
   <title>Dashboard</title>
</head>
<body>
	
  <tiles:insertAttribute name="content"/>	
   
</body>
</html>
