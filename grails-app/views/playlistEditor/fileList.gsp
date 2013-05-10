
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title></title>
</head>
<body>
<ul>
    <g:each in="${fileList}" var="file">
        <li>
            ${file.name}
        </li>
    </g:each>
</ul>
</body>
</html>