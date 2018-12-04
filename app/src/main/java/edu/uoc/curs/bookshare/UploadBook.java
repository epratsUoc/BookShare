package edu.uoc.curs.bookshare;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;

public class UploadBook extends AppCompatActivity {

    private static final String bookUrl = "https://www.googleapis.com/books/v1/volumes?q=isbn:";

    private Button scan;
    private TextView authorText, titleText, descriptionText, dateText, isbn10, isbn13;
    private ImageView thumbView;
    private String isbn;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_book);

        scan = findViewById(R.id.scan_button);
        authorText = findViewById(R.id.book_author);
        titleText = findViewById(R.id.book_title);
        descriptionText = findViewById(R.id.book_description);
        dateText = findViewById(R.id.book_date);
        isbn10 = findViewById(R.id.book_isbn10);
        isbn13 = findViewById(R.id.book_isbn13);
        thumbView = findViewById(R.id.thumb);

        Intent intent = getIntent();
        isbn = intent.getStringExtra("ISBN");

        //Se prepara la url para que el AsyncTask haga la búsqueda en la API Books
        //de Google, siempre de la misma manera.
        String bookSearch = bookUrl + isbn;
        //Mostraremos un diálogo para que el usuario sepa que está ocurriendo algo mientras
        //se ejecuta la AsyncTask
        dialog = ProgressDialog.show(UploadBook.this, "Please wait",
                "Searching your book...");

        //Ejecutamos AsynTask
        BookApi myTask = new BookApi();
        myTask.execute(bookSearch);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadBook.this, ScannerActivity.class);
                startActivity(intent);
            }
        });
    }

    private class BookApi extends AsyncTask<String, Object, JSONObject> {

        @Override
        protected void onPreExecute() {
            // TODO: Comprobar conexión a internet
        }

        @Override
        protected JSONObject doInBackground(String... bookUrl) {

            if (isCancelled()) {
                // TODO: Solucitud cancelada, hacer algo
                return null;
            }

            try {
                //Establecer conexión
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(bookUrl[0]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(5000);
                    connection.setConnectTimeout(5000);
                } catch (MalformedURLException e) {
                    //Este error no debería ocurrir nunca porque las URLs
                    //se generan siempre igual con la misma estrutura
                    // TODO: hacer algo con error
                    Log.e("BookApi.class", e.toString());
                } catch (ProtocolException e) {
                    //Este error no debería ocurrir nunca porque siempre se hace un
                    //GET y es un método válido
                    // TODO: hacer algo con error
                    Log.e("BookApi.class", e.toString());
                } catch (IOException e) {
                    // TODO: hacer algo con error
                    Log.e("BookApi.class", e.toString());
                }

                //Comprobamos que la respuesta es correcta 200OK
                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    Log.e("BookApi.class", "Request failed. Response Code: " + responseCode);
                    connection.disconnect();
                    return null;
                }

                //Leer datos
                StringBuilder builder = new StringBuilder();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = responseReader.readLine();
                while (line != null) {
                    builder.append(line);
                    line = responseReader.readLine();
                }
                String responseString = builder.toString();
                JSONObject responseJson = new JSONObject(responseString);

                //Cerrar conexión y devolver datos para que onPost los gestione
                connection.disconnect();
                return responseJson;

            } catch (SocketTimeoutException e) {
                // TODO: hacer algo con error
                Log.e("BookApi.class", e.toString());
                return null;
            } catch (IOException e) {
                // TODO: hacer algo con error
                Log.e("BookApi.class", e.toString());
                return null;
            } catch (JSONException e) {
                // TODO: hacer algo con error
                Log.e("BookApi.class", e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject responseJson) {
            if (isCancelled()) {
                // TODO: Hacer algo
            } else if (responseJson == null) {
                // TODO: Hacer algo, aquí se llega si la respuesta no es 200OK
            } else {
                try {
                    //La respuesta que da la API es un array, pero porque buscamos por
                    //ISBN este array debería tener un único libro siempre
                    JSONArray bookArray = responseJson.getJSONArray("items");
                    JSONObject bookObject = bookArray.getJSONObject(0);
                    JSONObject volumeObject = bookObject.getJSONObject("volumeInfo");

                    try {
                        titleText.setText(volumeObject.getString("title"));
                    } catch (JSONException jse) {
                        titleText.setText("TITLE NOT FOUND");
                        jse.printStackTrace();
                    }

                    //El libro puede tener más de un autor
                    try {
                        JSONArray authorArray = volumeObject.getJSONArray("authors");
                        StringBuilder authorBuild = new StringBuilder();
                        for (int i = 0; i < authorArray.length(); i++) {
                            if (i > 0) authorBuild.append(", ");
                            authorBuild.append(authorArray.getString(i));
                        }
                        authorText.setText(authorBuild.toString());
                    } catch (JSONException jse) {
                        authorText.setText("AUTHOR NOT FOUND");
                        jse.printStackTrace();
                    }

                    try {
                        dateText.setText(volumeObject.getString("publishedDate"));
                    } catch (JSONException jse) {
                        dateText.setText("PUBLICATION DATE NOT FOUND");
                        jse.printStackTrace();
                    }

                    try {
                        descriptionText.setText(volumeObject.getString("description"));
                    } catch (JSONException jse) {
                        descriptionText.setText("DESCRIPTION NOT FOUND");
                        jse.printStackTrace();
                    }

                    try {
                        JSONObject imageInfo = volumeObject.getJSONObject("imageLinks");
                        String imageUrl = imageInfo.getString("thumbnail");
                        Picasso.get().load(imageUrl).into(thumbView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //Los identificadores ISBN10 e ISBN13 no siempre están en el mismo orden
                    //en la respuesta que decibimos, por lo que el primer objeto dentro del
                    //array puede ser el 10 o el 13 indistintamente.
                    try {
                        JSONArray isbnArray = volumeObject.getJSONArray("industryIdentifiers");
                        JSONObject isbnObject1 = isbnArray.getJSONObject(0);
                        JSONObject isbnObject2 = isbnArray.getJSONObject(1);
                        if ("ISBN_13".equals(isbnObject1.getString("type"))) {
                            isbn10.setText("ISBN 10: " + isbnObject2.getString("identifier"));
                            isbn13.setText("ISBN 13: " + isbnObject1.getString("identifier"));
                        } else {
                            isbn10.setText("ISBN 10: " + isbnObject1.getString("identifier"));
                            isbn13.setText("ISBN 13: " + isbnObject2.getString("identifier"));
                        }
                    } catch (JSONException jse) {
                        isbn10.setText("ISBN10 NOT FOUND");
                        isbn13.setText("ISBN13 NOT FOUND");
                        jse.printStackTrace();
                    }

                    //Se cierra el diálogo cuando toda la información está lista para presentarse
                    //en pantalla
                    dialog.dismiss();
                } catch (JSONException e) {
                    // TODO: hacer algo con error
                    titleText.setText("BOOK NOT FOUND");
                    dialog.dismiss();
                    Log.e("BookApi.class", e.toString());
                }
            }
        }
    }
}
