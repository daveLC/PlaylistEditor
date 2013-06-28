
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title></title>
    </head>
    <body>
        <h1>Playlist Editor</h1>
        <h2>Playlist</h2>
        <g:if test="${!playList}">
            <p>Winamp is not running, start Winamp to vew the playlist</p>
        </g:if>
        <ul>
            <g:each in="${playList}" var="playlistEntry">
                <li>
                    ${playlistEntry.artist} - ${playlistEntry.title}
                </li>
            </g:each>
        </ul>
        <h2>Files</h2>
        <ul>
            <g:each in="${fileList}" var="file">
                <li>
                    ${file.name}
                </li>
            </g:each>
        </ul>
    </body>
</html>