package br.com.procob.search;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import br.com.procob.search.model.Response;
import br.com.procob.search.model.ResponseContent;
import br.com.procob.search.util.StringUtils;

public class MainApplication {

    private static final String API_URL = "https://api.procob.com/consultas/v2/L0004";
    private static final String CHARSET = "UTF-8";

    private static final Gson gson = new Gson();

    public static void main ( String[] args ) throws Exception {
        Scanner scanner = new Scanner( System.in );
        //scanner.useDelimiter( "\r\n" );

        System.out.println( "Seja bem-vindo a aplicação de pesquisa de telefones e endereço via Procob." );
        System.out.println( "---" );
        System.out.println( "Esta aplicação irá ler os nomes das pessoas da planilha que você informar e" );
        System.out.println( "gerar uma nova planilha com os dados encontrados." );
        System.out.println( "A planilha será gerada dentro da pasta onde está localizada este executor." );
        System.out.println( "Observação: quanto maior a planilha, mais pesado será para executar." );
        System.out.println( "---" );
        System.out.println( "Digite as informações solicitadas e pressione ENTER" );
        System.out.println( "---" );

        System.out.println( "Usuário:" );
        String username = scanner.next();

        System.out.println( "Senha:" );
        String password = scanner.next();

        if ( !StringUtils.hasText( username ) || !StringUtils.hasText( password ) ) {
            scanner.close();
            System.out.println( "Usuário e senha devem ser informados" );
            return;
        }

        System.out.println( "Informe o caminho da planilha. Para facilitar, arraste o arquivo até aqui." );
        String filePath = scanner.next().replaceAll( "\\\\", "\\\\\\\\" ).replaceAll( "\"", "" );

        System.out.println( "Informe S ou N para definir se o arquivo possui a primeira linha como cabeçalho" );
        String firstRowHeader = scanner.next();
        while ( !firstRowHeader.equalsIgnoreCase( "s" ) && !firstRowHeader.equalsIgnoreCase( "n" ) ) {
            firstRowHeader = scanner.next();
        }
        boolean isFirstRowHeader = firstRowHeader.equalsIgnoreCase( "s" );

        System.out.println( "Informe o número (ordem) da coluna do nome da pessoa:" );
        int nameCellNumber = Integer.parseInt( scanner.next() );

        scanner.close();

        File file = new File( filePath );
        Workbook workbook = WorkbookFactory.create( file );
        DataFormat cellFormatter = workbook.createDataFormat();

        CellStyle textStyle = workbook.createCellStyle();
        textStyle.setDataFormat( cellFormatter.getFormat( "@" ) );

        Sheet sheet = workbook.getSheetAt( 0 );

        int indexDocumentCell = 0;
        int indexPhoneCell = 0;
        int indexAddressCell = 0;

        boolean firstRow = true;
        for ( Row row : sheet ) {
            if ( firstRow && isFirstRowHeader ) {
                indexDocumentCell = row.getLastCellNum();
                Cell documentCell = row.createCell( indexDocumentCell );
                documentCell.setCellValue( "Documento" );

                indexPhoneCell = row.getLastCellNum();
                Cell phoneCell = row.createCell( indexPhoneCell );
                phoneCell.setCellValue( "Telefone(s)" );

                indexAddressCell = row.getLastCellNum();
                Cell addressCell = row.createCell( indexAddressCell );
                addressCell.setCellValue( "Endereço(s)" );

                firstRow = false;
                continue;
            } else if ( firstRow ) {
                firstRow = false;
            }

            Cell cellName = row.getCell( nameCellNumber - 1 );
            if ( cellName == null ) {
                break;
            }
            String name = cellName.getStringCellValue();
            if ( name == null ) {
                break;
            }
            name = name.trim();

            if ( !StringUtils.hasText( name ) ) {
                continue;
            }
            System.out.println( name );

            URL url = new URL( API_URL + "?" + String.format( "name=%s", URLEncoder.encode( name, CHARSET ) ) );
            HttpURLConnection connection = ( HttpURLConnection ) url.openConnection();

            connection.setRequestMethod( "GET" );
            String basicAuth = Base64.getEncoder()
                .encodeToString( ( username + ":" + password )
                .getBytes( StandardCharsets.UTF_8 ) );
            connection.setRequestProperty( "Authorization", "Basic " + basicAuth );

            InputStream in = new BufferedInputStream( connection.getInputStream() );
            JsonObject responseBody = gson.fromJson( new InputStreamReader( in ), JsonObject.class );

            Response response = gson.fromJson( responseBody, Response.class );

            if ( response.getCode().equals( "001" ) ) {
                System.out.println( response.getMessage() );
                continue;
            }

            if ( !response.getCode().equals( "000" ) ) {
                System.out.println( "Erro: " + response.getMessage() + ". A execução será interrompida" );
                break;
            }

            if ( response.getContent() == null || response.getContent().isEmpty() ) {
                continue;
            }

            System.out.println( responseBody.get( "content" ) );

            Cell documentCell = row.createCell( indexDocumentCell == 0 ? row.getLastCellNum() - 1 : indexDocumentCell );
            documentCell.setCellStyle( textStyle );
            Cell phoneCell = row.createCell( indexPhoneCell == 0 ? row.getLastCellNum() - 1 : indexPhoneCell );
            phoneCell.setCellStyle( textStyle );
            Cell addressCell = row.createCell( indexAddressCell == 0 ? row.getLastCellNum() - 1 : indexAddressCell );

            StringBuilder document = new StringBuilder( "" );
            List< String > phones = new ArrayList<>();
            List< String > addresses = new ArrayList<>();
            response.getContent().forEach( content -> {
                if ( StringUtils.hasText( content.getDocument() ) ) {
                    document.append( content.getDocument() );
                }

                if ( StringUtils.hasText( content.getDdd() ) ) {
                    phones.add( "(" + content.getDdd() + ") " + content.getPhone() );
                } else if ( StringUtils.hasText( content.getPhone() ) ) {
                    phones.add( content.getPhone() );
                }
                if ( StringUtils.hasText( content.getAddress() ) ) {
                    addresses.add( formatAddress( content ) );
                }
            } );

            documentCell.setCellValue( document.toString() );
            phoneCell.setCellValue( String.join( ", ", phones ) );
            addressCell.setCellValue( String.join( ", ", addresses ) );
        }

        workbook.close();
    }

    private static String formatAddress ( ResponseContent content ) {
        StringBuilder address = new StringBuilder( content.getAddress() );

        if ( StringUtils.hasText( content.getNumber() ) ) {
            address.append( " " ).append( content.getNumber() );
        }

        if ( StringUtils.hasText( content.getPlace() ) ) {
            address.append( " - " ).append( content.getPlace() );
        }

        if ( StringUtils.hasText( content.getDistrict() ) ) {
            address.append( " - " ).append( content.getDistrict() );
        }

        if ( StringUtils.hasText( content.getZipCode() ) ) {
            address.append( " - " ).append( content.getZipCode() );
        }

        if ( StringUtils.hasText( content.getComplement() ) ) {
            address.append( " - " ).append( content.getComplement() );
        }

        if ( StringUtils.hasText( content.getCity() ) ) {
            address.append( " - " ).append( content.getCity() );
        }

        if ( StringUtils.hasText( content.getState() ) ) {
            address.append( " - " ).append( content.getState() );
        }

        return address.toString();
    }

}
