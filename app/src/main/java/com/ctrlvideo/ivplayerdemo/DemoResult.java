package com.ctrlvideo.ivplayerdemo;

import java.util.List;

public class DemoResult {

    public String status;
    public List<Project> result;


    public class Project {
        public String pid;
        public String video_title;
        public String video_url;
    }
}
