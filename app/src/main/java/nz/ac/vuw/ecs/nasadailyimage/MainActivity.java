package nz.ac.vuw.ecs.nasadailyimage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * <h1>Nasa Daily Image Main Activity</h1>
 * The program implements an application that
 * simply acquires the latest daily image from NASA RSS and displays the image
 * with its relative information (e.g, title, description and date) on the screen.
 * <p/>
 * <b>Note:</b>
 * 1. The program currently doesn't run correctly due to some codes are missing, please complete the
 * program in the lab session.
 * 2. All the missing parts are marked by the label "##Missing##", please search in the entire
 * project by using the keyword to assure completeness.
 * 3. Please demo your work to your lab tutor by running the application successfully.
 *
 * @author Aaron Chen
 * @version 1.0
 * @since 2015-08-31
 */
public class MainActivity extends AppCompatActivity {

   // private static final String URL = "http://www.nasa.gov/rss/dyn/shuttle_station.rss";
    private static final String URL = "http://www.nasa.gov/rss/dyn/image_of_the_day.rss";

    private Image image = null;
    TextView titleText;
    TextView imageDateText;
    TextView imageDecText;
    ImageView imageView;
    private ShareActionProvider mShareActionProvider;
    PhotoViewAttacher mAttacher;
    public String imageTitle = "Title";
    public String imgDec = "Image description";
    public String imgDate = "Image Date";
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);



        new MainTask().execute();
    }

//    SharedPreferences.OnSharedPreferenceChangeListener myPrefListner = new SharedPreferences.OnSharedPreferenceChangeListener(){
//        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
//            Log.i("Pref changed","");
//            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
//            String userSplashValue = preferences.getString("example_list", "1");
//            if(userSplashValue.equals("1"))
//            {
//                URL = "http://www.nasa.gov/rss/dyn/lg_image_of_the_day.rss";
//            }
//            else if(userSplashValue.equals("2"))
//            {
//                URL = "http://www.nasa.gov/rss/dyn/earth.rss";
//            }
//            else
//            {
//                URL = "http://www.nasa.gov/rss/dyn/shuttle_station.rss";
//            }
//            new MainTask().execute();
//        }
//    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_share);

        // Get its ShareActionProvider
        //mShareActionProvider = (ShareActionProvider) item.getActionProvider();
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        mShareActionProvider.setShareIntent(getDefaultIntent());


        return super.onCreateOptionsMenu(menu);

    }

//    @Override
//    protected void onStop(){
//        super.onStop();
//
//        // We need an Editor object to make preference changes.
//        // All objects are from android.context.Context
//        SharedPreferences.Editor editor = sharedpreferences.edit();
//        editor.putString(imageTitle, titleText.getText().toString());
//        editor.putString(imgDec, imageDecText.getText().toString());
//        editor.putString(imgDate, imageDateText.getText().toString());
//
//        editor.commit();
//    }

    private void updateSharedPrefs() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(imageTitle, titleText.getText().toString());
        editor.putString(imgDec, imageDecText.getText().toString());
        editor.putString(imgDate, imageDateText.getText().toString());

        editor.commit();
    }

    private Intent getDefaultIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        //intent.setType("text/plain");

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        intent.putExtra(Intent.EXTRA_SUBJECT, "A Title Of The Post");
        intent.putExtra(Intent.EXTRA_TEXT, "bla");

        // startActivity(Intent.createChooser(intent, "Share link!"));
        return intent;
    }



    private Intent updateShare() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        //intent.setType("image/*");
        intent.setType("text/plain");

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        String title = imageTitle;
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, imgDec + " \n" + image.getUrl());

        //startActivity(Intent.createChooser(intent, "Share link!"));

        return intent;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent setting = new Intent(this, SettingsActivity.class);
            startActivity(setting);
            return true;
        }

        if (id == R.id.action_save) {
            //Get image from image view, then save that image to the devices storage
            Drawable img = imageView.getDrawable();
            BitmapDrawable bitmapDrawable = ((BitmapDrawable) img);
            Bitmap bitmap = bitmapDrawable.getBitmap();
            MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, imageTitle, imgDec);

            //Show text saying it has been saved
            Context context = getApplicationContext();
            CharSequence text = "Image saved!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is used to reset the display on screen after
     * retrieving the image from RSS.
     *
     * @param
     * @return Nothing.
     */
    public void resetDisplay() {

        titleText = (TextView) findViewById(R.id.imageTitle);
        imageDateText = (TextView) findViewById(R.id.imageDate);
        imageDecText = (TextView) findViewById(R.id.imageDescription);
        imageView = (ImageView) findViewById(R.id.imageView);

        if (image.getDate() instanceof String) {
            titleText.setText(image.getTitle());
            imageTitle = image.getTitle();
            imageDateText.setText(image.getDate());
            imgDate = image.getDate();
            imageDecText.setText(image.getDescription());
            imgDec = image.getDescription();
           // updateSharedPrefs();
           // updateSharedPrefs();
        } else {
            titleText.setText("Image title");
            imageDateText.setText("Image Date");
            imageDecText.setText("Image Description");
        }


       // if (image.getUrl() != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int height = metrics.heightPixels;
            int width = metrics.widthPixels;

            //Display the image by its url
            new ImageDownloader(imageView, width, height).execute(image.getUrl());
            // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
            mAttacher = new PhotoViewAttacher(imageView);
       // }

    }

    /**
     * This inner class inherits from AsyncTask which performs background
     * operations and publish results on the UI thread.
     */
    public class MainTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... params) {

            //Invoke the function to retrieve the image from NASA RSS feed.
            processFeed();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);
            resetDisplay();
            // updateSharedPrefs();
            mShareActionProvider.setShareIntent(updateShare());
        }

        /**
         * This method is used to retrieve the latest daily image from NASA RSS feed.
         *
         * @param
         * @return Nothing.
         */
        public void processFeed() {
            try {
                SAXParserFactory saxParserFactory =
                        SAXParserFactory.newInstance();
                SAXParser parser = saxParserFactory.newSAXParser();
                XMLReader reader = parser.getXMLReader();
                IotdHandler iotdHandler = new IotdHandler();
                reader.setContentHandler(iotdHandler);

                InputStream inputStream = new URL(URL).openStream();
                reader.parse(new InputSource(inputStream));


                image = iotdHandler.getImage();
//                Log.i("Image was null", "Image was null " + image.getTitle());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    int width = 0;
    int height = 0;


    public ImageDownloader(ImageView bmImage, int width, int height) {
        this.bmImage = bmImage;
        this.width = width;
        this.height = height;
    }

    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap mIcon = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            mIcon = BitmapFactory.decodeStream(in);
            int imgWidth = mIcon.getWidth();
            int imgHeight = mIcon.getHeight();


            Bitmap bMapScaled = Bitmap.createScaledBitmap(mIcon, width, ((imgHeight / imgWidth) * width), true);
            mIcon = bMapScaled;
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        return mIcon;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}


