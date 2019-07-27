package com.project.twitter.common;

import org.apache.commons.fileupload.FileItem;

public class GeneralUtil {
    public static final int defaultCurrentPage = 1;
    public static final String CHARSET = "UTF-8";

    public static String getQueryBuilder(String query, int recordsPerPage, int start) {
        return String.format(query, "LIMIT " + recordsPerPage + " OFFSET " + start);
    }

    public static String getFileExtension(FileItem fileItem) {
        return fileItem.getName().substring(fileItem.getName().lastIndexOf("."));
    }
}