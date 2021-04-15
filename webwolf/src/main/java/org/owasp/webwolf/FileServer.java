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

package org.owasp.webwolf;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.owasp.webwolf.user.WebGoatUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;

/**
 * Controller for uploading a file
 */
@Controller
@Slf4j
public class FileServer {

    @Value("${webwolf.fileserver.location}")
    private String fileLocatation;
    @Value("${server.address}")
    private String server;
    @Value("${server.port}")
    private int port;


    @PostMapping(value = "/WebWolf/fileupload")
    @SneakyThrows
    public ModelAndView importFile(@RequestParam("file") MultipartFile myFile) {
        WebGoatUser user = (WebGoatUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        File destinationDir = new File(fileLocatation, user.getUsername());
        destinationDir.mkdirs();
        myFile.transferTo(new File(destinationDir, myFile.getOriginalFilename()));
        log.debug("File saved to {}", new File(destinationDir, myFile.getOriginalFilename()));
        Files.touch(new File(destinationDir, user.getUsername() + "_changed"));

        ModelMap model = new ModelMap();
        model.addAttribute("uploadSuccess", "File uploaded successful");
        return new ModelAndView(
                new RedirectView("files", true),
                model
        );
    }

    @AllArgsConstructor
    @Getter
    private class UploadedFile {
        private final String name;
        private final String size;
        private final String link;
    }

    @GetMapping(value = "/WebWolf/files")
    public ModelAndView getFiles(HttpServletRequest request) {
        WebGoatUser user = (WebGoatUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = user.getUsername();
        File destinationDir = new File(fileLocatation, username);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("files");
        File changeIndicatorFile = new File(destinationDir, user.getUsername() + "_changed");
        if (changeIndicatorFile.exists()) {
            modelAndView.addObject("uploadSuccess", request.getParameter("uploadSuccess"));
        }
        changeIndicatorFile.delete();

        List<UploadedFile> uploadedFiles = Lists.newArrayList();
        File[] files = destinationDir.listFiles(File::isFile);
        if (files != null) {
            for (File file : files) {
                String size = FileUtils.byteCountToDisplaySize(file.length());
                String link = String.format("files/%s/%s", username, file.getName());
                uploadedFiles.add(new UploadedFile(file.getName(), size, link));
            }
        }

        modelAndView.addObject("files", uploadedFiles);
        modelAndView.addObject("webwolf_url", "http://" + server +":" + port);
        return modelAndView;
    }
}