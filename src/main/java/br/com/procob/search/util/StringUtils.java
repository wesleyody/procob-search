package br.com.procob.search.util;

public class StringUtils {

    public static boolean hasText ( String text ) {
        return text != null && !text.trim().equals( "" );
    }

}
