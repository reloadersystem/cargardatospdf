package resembrink.dev.cargardatospdf;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //www.youtube.com/watch?v=38FDZTUKv3E; tcrurav

    ImageView image_descarga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image_descarga=findViewById(R.id.image_descarga);
        image_descarga.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.image_descarga:
                descargarPDF();
                break;

        }
    }

    private void descargarPDF() {

        String urlADescargar="http://www4.tecnun.es/asignaturas/Informat1/AyudaInf/aprendainf/Java/Java2.pdf";

        //creo un alertdialog tipo Progress
        ProgressDialog progressDialog= new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("Descargando PDF..");


        //new DescargarPDFAsynTask().execute(urlADescargar);  sin progressdialog
        new DescargarPDFAsynTask(progressDialog).execute(urlADescargar);

    }

    class DescargarPDFAsynTask extends AsyncTask<String , Integer, String>
        //1er Void lo que le quiero pasar doInBackGraound
            // 2do Void lo que le quiero pasar onProgressUpdate
            //3er Void lo que le quiero pasar doonPostExecute


    {
        ProgressDialog progressDialog;

        public DescargarPDFAsynTask(ProgressDialog progressDialog) {

            this.progressDialog=progressDialog;

        }

        @Override
        protected void onPreExecute() {
            //lo que quiero hacer antes de la descarga
            super.onPreExecute();

            //arrancar el progressdialog
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... urlPDF) {
            //String... url  antes  Void  array de dimension desconocida
            //as en segund plano doInBackground


            //declaramos  y inicializamos los  datos
            HttpURLConnection conexion= null;
            InputStream input= null;
            OutputStream output= null;


            String urlADescargar= urlPDF[0];
            try {
                URL url= new URL(urlADescargar);
                 conexion= (HttpURLConnection) url.openConnection(); //abrir la conexion
                 conexion.connect(); // conectar
                        //tengo creada la  conexion
                if(conexion.getResponseCode() != HttpURLConnection.HTTP_OK) //si me  conecte bien
                {
                    return "Conexion no realizada correctamente";

                }

                //definir de donde descargo el inputstream es de una conexion httpURL de una conexion
                // de  internet al instituto donde esta el pdf, es decir le  digo de donde lo va descargar
                input= conexion.getInputStream();

                String rutaficheroGuardado= getFilesDir() + "/TablaDeConvalidaciones.pdf"; //data/data/resebrink.dev.cargadatopdf/
                //getFilesDire( carpeta donde esta mi app) y le damos  un nombre

                //lugar fisico  donde  almaceno
                 output= new FileOutputStream(rutaficheroGuardado);

                 //tamaño del  fichero
                 int tamanoFichero=conexion.getContentLength();

                //descargarme el fichero pdf
                byte[] data = new byte[1024]; //1024 en  1024 bytes
                int count;
                int total=0;
                //-1 quiere decir que todavia hay paquetes para descargar
                while((count= input.read(data)) != -1)
                {
                    sleep(1); // el progressbar para que  vaya de 0 a  100 % //velocidad de descarga 100 mas lento  // 1000  mas lento
                    output.write(data,0,count);
                    //llamando al progressupdate
                    total += count;
                    publishProgress((int)(total*100/tamanoFichero));

                    //lo que pesa el archivo count
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
            //cerrando la carpeta una vez que termino
            catch (InterruptedException e) {
                e.printStackTrace();
            } finally {

                    try {
                        if(input!= null) input.close();
                        if(output!=null) output.close();
                        if(conexion!=null) conexion.disconnect();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }



            return "Se realizo Correctamente";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //% de descarga
            super.onProgressUpdate(values);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String mensaje) {
            //despues  de que termine la descarga
            super.onPostExecute(mensaje);

            progressDialog.dismiss();

            Toast.makeText(MainActivity.this, mensaje, Toast.LENGTH_SHORT).show();
        }

    }
}
