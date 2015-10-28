/*
 * Copyright 2015 Alexandre Terrasa <alexandre@moz-code.org>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package inf4375.server.controllers;

import inf4375.server.Request;
import inf4375.server.Router;
import inf4375.server.UriMatchController;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

/**
 * A Controller that displays the catalog in JSON format.
 */
public class AlbumsController extends UriMatchController {

    // Json array used to store the albums list.
    // 
    // TODO this should be in the model layer of this application
    JsonArray catalog;

    public AlbumsController(JsonArray catalog) {
        this.uriMatch = "^/albums/(\\d+)$";
        this.catalog = catalog;
    }

    @Override
    public void action(Router router, Request request) {
        // Parse album URI
        String uri = request.uri;
        String id;
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(uri);
        if (!matcher.find()) {
            router.sendJsonError(400, "Bad request");
            return;
        }
        id = matcher.group(1);
        
        switch(request.method) {
            case "GET":
                actionGetAlbum(router, request, id);
                return;
            case "POST":
                actionPostAlbum(router, request, id);
                return;
            case "PUT":
                actionPutAlbum(router, request, id);
                return;
            case "DELETE":
                actionDeleteAlbum(router, request, id);
                return;
        }
        router.sendJsonError(400, "Bad request");
    }
    
    // Returns the JSON Object representing the album having `id`.
    private void actionGetAlbum(Router router, Request request, String id) {
        for (JsonObject album : catalog.getValuesAs(JsonObject.class)) {
            if (album.getString("id").equals(id)) {
                router.sendJsonResponse(200, "OK", album);
                return;
            }
        }
        router.sendJsonError(404, "Not found");
    }

    // Create a new album from a JsonRequest.
    //
    // Display the received album.
    private void actionPostAlbum(Router router, Request request, String id) {
        JsonReader reader = Json.createReader(new StringReader(request.body));
        JsonObject album = reader.readObject();
        // TODO add model layer and data management
        router.sendJsonResponse(201, "Created", album);
    }
    
    // Update the existing album with `id`.
    //
    // Display the modified album.
    private void actionPutAlbum(Router router, Request request, String id) {
        JsonReader reader = Json.createReader(new StringReader(request.body));
        JsonObject album = reader.readObject();
        for (JsonObject oalbum : catalog.getValuesAs(JsonObject.class)) {
            if (oalbum.getString("id").equals(id)) {
                JsonObjectBuilder builder = Json.createObjectBuilder();
                for(String key: oalbum.keySet()) {
                    builder.add(key, oalbum.get(key));
                }
                for(String key: album.keySet()) {
                    builder.add(key, album.get(key));
                }
                // TODO add model layer and data management
                router.sendJsonResponse(200, "OK", builder.build());
                return;
            }
        }
        // No album with this ID found, return error
        router.sendJsonError(404, "Not found");
    }
    
    // Update the existing album with `id`.
    //
    // Display the deleted album.
    private void actionDeleteAlbum(Router router, Request request, String id) {
        for (JsonObject oalbum : catalog.getValuesAs(JsonObject.class)) {
            if (oalbum.getString("id").equals(id)) {
                // TODO add model layer and data management
                router.sendJsonResponse(200, "OK", oalbum);
                return;
            }
        }
        // No album with this ID found, return error
        router.sendJsonError(404, "Not found");
    }
}
