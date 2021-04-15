/*
 * This file is part of WebGoat, an Open Web Application Security Project utility. For details, please see http://www.owasp.org/
 *
 * Copyright (c) 2002 - 2019 Bruce Mayhew
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * Getting Source ==============
 *
 * Source for this application is maintained at https://github.com/WebGoat/WebGoat, a repository for free software projects.
 */

package org.owasp.webgoat.idor;


import org.owasp.webgoat.assignments.AssignmentEndpoint;
import org.owasp.webgoat.assignments.AssignmentHints;
import org.owasp.webgoat.assignments.AttackResult;
import org.owasp.webgoat.session.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@AssignmentHints({"idor.hints.ownProfileAltUrl1", "idor.hints.ownProfileAltUrl2", "idor.hints.ownProfileAltUrl3"})
public class IDORViewOwnProfileAltUrl extends AssignmentEndpoint {

    @Autowired
    UserSessionData userSessionData;

    @PostMapping("IDOR/profile/alt-path")
    @ResponseBody
    public AttackResult completed(@RequestParam String url) {
        try {
            if (userSessionData.getValue("idor-authenticated-as").equals("tom")) {
                //going to use session auth to view this one
                String authUserId = (String) userSessionData.getValue("idor-authenticated-user-id");
                //don't care about http://localhost:8080 ... just want WebGoat/
                String[] urlParts = url.split("/");
                if (urlParts[0].equals("WebGoat") && urlParts[1].equals("IDOR") && urlParts[2].equals("profile") && urlParts[3].equals(authUserId)) {
                    UserProfile userProfile = new UserProfile(authUserId);
                    return trackProgress(success().feedback("idor.view.own.profile.success").output(userProfile.profileToMap().toString()).build());
                } else {
                    return trackProgress(failed().feedback("idor.view.own.profile.failure1").build());
                }

            } else {
                return trackProgress(failed().feedback("idor.view.own.profile.failure2").build());
            }
        } catch (Exception ex) {
            return failed().feedback("an error occurred with your request").build();
        }
    }
}
