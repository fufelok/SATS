package se.leanbit.sats;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Config;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.PlayerStyle;
import com.google.android.youtube.player.YouTubePlayerView;


import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.PlayerStyle;
import com.google.android.youtube.player.YouTubePlayerView;


import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Timer;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.google.android.youtube.player.YouTubePlayer;

import se.leanbit.sats.models.SatsActivity;
import se.leanbit.sats.models.*;

import se.leanbit.sats.repositories.services.SatsActivitiesService;
import se.leanbit.sats.repositories.services.WebService;


public class BookedActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener, YouTubeThumbnailView.OnInitializedListener
{
    private static final String YOUTUBE_API_KEY = "AIzaSyCJxCUMuVKTx0RM8EyNOMeNpdfKHkbYcKc";
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    private String vidId;

    final String classTypeUrl = "https://api2.sats.com/v1.0/se/classTypes";
    private FullClassTypes fct;
    private int classIndex;

    private YouTubePlayer youTubePlayer;
    private YouTubeThumbnailLoader youTubeThumbnailLoader;

    private ProgressBar cardioBar;
    private ProgressBar strenghtBar;
    private ProgressBar flexibilityBar;
    private ProgressBar balanceBar;
    private ProgressBar resilienceBar;

    private TextView que;
    private TextView className;
    private TextView duration;
    private TextView dateText;
    private TextView centerName;
    private TextView description;
    private TextView instructorName;
    private TextView participationAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Gson gson = new GsonBuilder().create();

        Log.e("onCreate", "on Create was called");

        try
        {
            fct = gson.fromJson(new WebService().execute(classTypeUrl).get(), FullClassTypes.class);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        } catch (ExecutionException e)
        {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        SatsActivity activity = (SatsActivity) intent.getSerializableExtra("Activity");
        setContentView(R.layout.booked_class);

        findClassIndex(activity);

        setPortretItems();
        setPortretItemvalues(activity);
        setProgressBars();
        initializeVideo();


    }

    private void initializeVideo()
    {

        String fullVidUrl = fct.classTypes[classIndex].videoUrl;
        int endPoint = fullVidUrl.indexOf("?");
        int startPoint = fullVidUrl.indexOf("embed/");
        vidId = fullVidUrl.substring(startPoint + 6, endPoint);

        final ImageView play = (ImageView) findViewById(R.id.booked_class_play_button);

        final YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.booked_class_video_player);
        youTubePlayerView.setVisibility(youTubePlayerView.GONE);
        final YouTubeThumbnailView youTubeThumbnailView = (YouTubeThumbnailView) findViewById(R.id.booked_class_video_thumbnail);
        youTubeThumbnailView.initialize(YOUTUBE_API_KEY, this);

        play.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                if (youTubePlayer != null)
                {

                    youTubeThumbnailView.setVisibility(view.GONE);
                    play.setVisibility(view.GONE);
                    youTubePlayerView.setVisibility(view.VISIBLE);

                    youTubePlayer.loadVideo(vidId);
                }
            }
        });
        youTubePlayerView.initialize(Config.DEVELOPER_KEY, this);
    }

    private void setPortretItems()
    {
        que = (TextView) findViewById(R.id.booked_class_que_text);
        className = (TextView) findViewById(R.id.booked_class_class_name);
        duration = (TextView) findViewById(R.id.booked_class_class_duration);
        dateText = (TextView) findViewById(R.id.booked_class_date_name);
        centerName = (TextView) findViewById(R.id.booked_class_center_name);
        description = (TextView) findViewById(R.id.booked_class_description);
        instructorName = (TextView) findViewById(R.id.booked_class_instructor_name);
        participationAmount = (TextView) findViewById(R.id.booked_class_review_num);
    }

    private void setPortretItemvalues(SatsActivity activity)
    {

        que.setText("" + activity.booking.positionInQueue);
        duration.setText("" + activity.durationInMinutes + " min");
        className.setText(activity.subType);
        description.setText(fct.classTypes[classIndex].description);
        dateText.setText(activity.date);
        centerName.setText(new SatsActivitiesService().getRegion(activity));
        instructorName.setText(activity.booking.clazz.instructorId);
        participationAmount.setText(""+activity.booking.clazz.bookedPersonsCount+"st utav max "
            +activity.booking.clazz.maxPersonsCount+" har anmält sig");
    }

    private void setProgressBars()
    {
        cardioBar = (ProgressBar) findViewById(R.id.booked_class_fitness_progress);
        strenghtBar = (ProgressBar) findViewById(R.id.booked_class_strength_progress);
        flexibilityBar = (ProgressBar) findViewById(R.id.booked_class_agility_progress);
        balanceBar = (ProgressBar) findViewById(R.id.booked_class_balance_progress);
        resilienceBar = (ProgressBar) findViewById(R.id.booked_class_resilience_progress);
        for (int i = 0; i < this.fct.classTypes[classIndex].profile.length; i++)
        {
            switch (this.fct.classTypes[classIndex].profile[i].id)
            {
                case "cardio":
                    cardioBar.setProgress(this.fct.classTypes[classIndex].profile[0].value);
                    break;

                case "strength":
                    strenghtBar.setProgress(this.fct.classTypes[classIndex].profile[1].value);
                    break;

                case "flexibility":
                    flexibilityBar.setProgress(this.fct.classTypes[classIndex].profile[2].value);
                    break;

                case "balance":
                    balanceBar.setProgress(this.fct.classTypes[classIndex].profile[3].value);
                    break;

                case "agility":
                    resilienceBar.setProgress(this.fct.classTypes[classIndex].profile[4].value);
                    break;
            }
        }
    }

    private void findClassIndex(SatsActivity activity)
    {
        for (int i = 0; i < fct.classTypes.length; i++)
        {
            if (fct.classTypes[i].name.equals(activity.subType))
            {
                classIndex = i;
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_booked, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason)
    {
        if (errorReason.isUserRecoverableError())
        {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else
        {
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer player, boolean wasRestored)
    {
        youTubePlayer = player;

        if (!wasRestored)
        {
            player.setPlayerStyle(PlayerStyle.MINIMAL);
            if (!wasRestored)
            {
                player.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION + YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);

                player.cueVideo(vidId);
            }
        }
    }

    @Override
    public void onInitializationSuccess(YouTubeThumbnailView thumbnailView,
                                        YouTubeThumbnailLoader thumbnailLoader)
    {
        youTubeThumbnailLoader = thumbnailLoader;
        thumbnailLoader.setOnThumbnailLoadedListener(new ThumbnailLoadedListener());

        youTubeThumbnailLoader.setVideo(vidId);

    }

    @Override
    public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult)
    {

    }


    @Override
    protected void onPause()
    {
        super.onPause();

    }

    public class Config
    {
        // Google Console APIs developer key
        // Replace this key with your's
        public static final String DEVELOPER_KEY = "AIzaSyCJxCUMuVKTx0RM8EyNOMeNpdfKHkbYcKc";

        // YouTube video id
        //public static final String YOUTUBE_VIDEO_CODE = "1H_znJi2nbE";
    }

    private final class ThumbnailLoadedListener implements
            YouTubeThumbnailLoader.OnThumbnailLoadedListener
    {

        @Override
        public void onThumbnailError(YouTubeThumbnailView arg0, YouTubeThumbnailLoader.ErrorReason arg1)
        {
        }

        @Override
        public void onThumbnailLoaded(YouTubeThumbnailView arg0, String arg1)
        {
        }

    }
}
