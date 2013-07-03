
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>Playlist Editor</title>
    </head>
    <body>
        <h1>Playlist Editor</h1>
        <h2>Playlist</h2>
        <g:if test="${!playList}">
            <p>Winamp is not running, start Winamp to vew the playlist</p>
        </g:if>
        <ol>
            <g:each in="${playList}" var="playlistEntry" status="i">
                <li ${playlistEntry.isCurrent ? 'class="current"' : ''}>
                    ${playlistEntry.artist} - ${playlistEntry.title}
                </li>
            </g:each>
        </ol>
        <h2>Files</h2>
        <ol>
            <g:each in="${trackList}" var="track" status="i">
                <li>
                    <g:link action="addToPlaylist" params="[filename:track.path]">${track.name}</g:link>
                </li>
            </g:each>
        </ol>
    </body>
</html>