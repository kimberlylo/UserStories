package com.example.lkimberly.userstories.fragments;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lkimberly.userstories.R;
import com.example.lkimberly.userstories.adapters.SwipeCardAdapter;
import com.example.lkimberly.userstories.models.Job;
import com.example.lkimberly.userstories.models.Matches;
import com.example.lkimberly.userstories.models.SwipeCard;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class FeedFragment extends Fragment {

    ArrayList<SwipeCard> al;
    SwipeCardAdapter swipeCardAdapter;
    int i;

    SwipeFlingAdapterView flingContainer;

    ParseUser currentUser;
    ArrayList<Job> jobs;

    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    SphericalUtil mapsUtil;
    Location userCurentLocation;

    private FusedLocationProviderClient mFusedLocationProvidentClient;
    private static final String TAG = "FeedFragment";

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_feed, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
        getDeviceLocation();

        jobs = new ArrayList<>();

        currentUser = ParseUser.getCurrentUser();

        final Job.Query postsQuery = new Job.Query();
        postsQuery.getTop().withUser();

        al = new ArrayList<SwipeCard>();

//        loadTopPosts();

        swipeCardAdapter = new SwipeCardAdapter(getContext(), getLayoutInflater(), al);

        flingContainer = getActivity().findViewById(R.id.frame);

        flingContainer.setAdapter(swipeCardAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                SwipeCard temp = al.remove(0);
                swipeCardAdapter.notifyDataSetChanged();
                al.add(temp);
                swipeCardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                makeToast(getContext(), "Left!");
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                SwipeCard currentCard = (SwipeCard) dataObject;
                createMatch(currentCard);
                makeToast(getContext(), "Right!");
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
//                 Ask for more data here
                Log.d("onAdapterAboutToEmpty", "No more!");
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = flingContainer.getRootView();
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                makeToast(getContext(), "Details");
                swipeCardAdapter.goToDetailsPage(((SwipeCard) dataObject).getJob());
            }
        });


        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);


    }

    private void createMatch(SwipeCard currentCard) {
        final Matches newMatch = new Matches();
        newMatch.setJobPoster(currentCard.getJob().getUser());
        newMatch.setJobSubscriber(currentUser);
        newMatch.setJob(currentCard.getJob());

        Log.d("newMatchSave", "1. Success!");

        newMatch.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("createMatch", "save match success!");
//                    Toast.makeText(getContext(), "Match saved", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("createMatch", "save match failed!");
                    e.printStackTrace();
                }
            }
        });
    }

    static void makeToast(Context ctx, String s){
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }

    private void loadTopPosts() {
        final Job.Query postsQuery = new Job.Query();
        postsQuery.getTop().withUser();
        final List<SwipeCard> tempList = new ArrayList<>();
        postsQuery.findInBackground(new FindCallback<Job>() {
            @Override
            public void done(List<Job> objects, ParseException e) {
                if (e == null) {

                    for (int i = 0; i < objects.size(); ++i) {

                        Job job = objects.get(objects.size() - i - 1);

                        try {
                            al.add(new SwipeCard(job.getTitle().toString(), job.getDescription().toString(), job.getImage().getUrl(), job));
                            rankList(al);
                            Collections.reverse(al);
                        } catch (NullPointerException e2) {
                            rankList(al);
                            Collections.reverse(al);
                            al.add(new SwipeCard("EMPTY", "EMPTY", "EMPTY", null));
                        }
                        swipeCardAdapter.notifyDataSetChanged();
                    }
                    int count = 1;
                    for (SwipeCard card: al) {
                        Log.d("Card"+count, String.valueOf(getFOneScore(card.getJob())));
                        count++;
                    }

                } else {
                    e.printStackTrace();
                }
            }
        });
    }


    private void rankList(ArrayList<SwipeCard> jobList) {
        Collections.sort(jobList, new Comparator<SwipeCard>() {
            @Override
            public int compare(SwipeCard sc1, SwipeCard sc2) {
                return Double.compare(getFOneScore(sc1.getJob()), getFOneScore(sc2.getJob()));
            }
        });
    }

    private double getFOneScore(Job jobToComputeScore) {
        double distanceInKm;
        double userRatingForScore;

        try {
            try {
                distanceInKm = mapsUtil.computeDistanceBetween(new LatLng(Double.valueOf((String) jobToComputeScore.get("latitude")), Double.valueOf((String) jobToComputeScore.get("longitude"))),
                        new LatLng(userCurentLocation.getLatitude(), userCurentLocation.getLongitude()))/1000;
            } catch (NullPointerException nullEx) {
                distanceInKm = 1;
                nullEx.printStackTrace();
            }

        } catch (SecurityException secEx) {
            distanceInKm = 1;   // ignore distance in calcualtion
            secEx.printStackTrace();
        }

        double inverseDist = 1/distanceInKm;
        try {
            String retrievedRating = (String) ((ParseUser) jobToComputeScore.get("user")).fetchIfNeeded().get("rating");
            if (retrievedRating == null) {
                userRatingForScore = 0;
            } else {
                double ratingFraction = parseDouble(retrievedRating);
                userRatingForScore = ratingFraction*5;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
        Log.d((String) jobToComputeScore.get("title"), distanceInKm + ", " + inverseDist + ", " + userRatingForScore);
        double score = (inverseDist*userRatingForScore)/(inverseDist+userRatingForScore);
        return score;

    }

    double parseDouble(String ratio) {
        if (ratio.contains("/")) {
            String[] rat = ratio.split("/");
            return Double.parseDouble(rat[0]) / Double.parseDouble(rat[1]);
        } else {
            return Double.parseDouble(ratio);
        }
    }

    private void getDeviceLocation() {
        mFusedLocationProvidentClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try {
//            if (mLocationPermissionsGranted) {
            Task location = mFusedLocationProvidentClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            userCurentLocation = (Location) task.getResult();

                            double latitude = userCurentLocation.getLatitude();
                            double longitude = userCurentLocation.getLongitude();

                            StringBuilder result = new StringBuilder();

                            loadTopPosts();
                            try {
                                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                if (addresses.size() > 0) {
                                    Address address = addresses.get(0);
                                    Log.d(TAG, "onComplete: found location! " + task.getResult());
//                                    result.append(address.getLocality());
//                                    result.append(address.getCountryName());
                                }
                            } catch (IOException e) {
                                Log.e("tag", e.getMessage());
                            }

//                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");
                        }

                    } else {
                        Log.d(TAG, "onComplete: current location is null");
                        Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
//            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: Security Exception: " + e.getMessage());
        }
    }

}