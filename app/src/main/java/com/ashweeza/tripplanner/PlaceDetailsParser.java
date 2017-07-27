package com.ashweeza.tripplanner;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ashweeza on 2/25/2017.
 */

public class PlaceDetailsParser {
    static public class PlaceDetailsJsonParser {
        public static PlaceDetails getFeedArrayList(String in) throws JSONException {
            String IMAGE_BASE_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=500&maxheight=500&photoreference=";
            String PHOTO_REF_URL;
            String KEY_URL = "&key=AIzaSyCz19lSR3gBMyzIUmUg_kkSO_EU4LqgkdA";
            String FINAL_URL = "";
            PlaceDetails feedArrayList = new PlaceDetails();
            JSONObject root = new JSONObject(in);
            JSONObject placedetailsObject = root.getJSONObject("result");

            feedArrayList.setAddress(placedetailsObject.getString("formatted_address"));
            if (placedetailsObject.has("international_phone_number")) {
                feedArrayList.setPhonenumber(placedetailsObject.getString("international_phone_number"));
            } else {
                feedArrayList.setPhonenumber("0000");
            }
            if (placedetailsObject.has("website")) {
                feedArrayList.setWebsite(placedetailsObject.getString("website"));
            } else {
                feedArrayList.setWebsite("N/A");
            }
            if (placedetailsObject.has("opening_hours")) {
                JSONObject hoursobject = new JSONObject(placedetailsObject.getString("opening_hours"));
                JSONArray hoursarray = new JSONArray(hoursobject.getString("weekday_text"));
                String openinghours = "";
                for (int i = 0; i < hoursarray.length(); i++) {
                    if (i < hoursarray.length() - 1) {
                        openinghours = openinghours + hoursarray.getString(i) + "\n";
                    } else if (i == 6) {
                        openinghours = openinghours + hoursarray.getString(i);
                    }


                }
                Log.v("tag", "Opening hours String:" + openinghours);
                feedArrayList.setOpeninghours(openinghours);
            } else {
                feedArrayList.setOpeninghours("N/A");
            }


            if (placedetailsObject.has("photos")) {

                JSONArray photosarray = new JSONArray(placedetailsObject.getString("photos"));
                String photos[] = new String[photosarray.length()];


                for (int j = 0; j < photosarray.length(); j++) {
                    JSONObject photoobject = photosarray.getJSONObject(j);
                    String ref_value = photoobject.getString("photo_reference");
                    FINAL_URL = IMAGE_BASE_URL + ref_value + KEY_URL;
                    // feedArrayList.setIcon_url(FINAL_URL);
                    photos[j] = FINAL_URL;
                }
                feedArrayList.setPhotos(photos);
                // PHOTO_REF_URL = ref_value;

            } else {
                String photosss[] = new String[1];
                photosss[0] = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=100" +
                        "&photoreference=CnRtAAAATLZNl354RwP_9UKbQ_5Psy40texXePv4oAlgP4qNEkdIrkyse7rPXYGd9D_Uj1rVsQdWT4o" +
                        "Rz4QrYAJNpFX7rzqqMlZw2h2E2y5IKMUZ7ouD_SlcHxYq1yL4KbKUv3qtWgTK0A6QbGh87GB3sscrHRIQiG2RrmU_jF4tENr9wGS_" +
                        "YxoUSSDrYjWmrNfeEHSGSc3FyhNLlBU&key=AIzaSyCz19lSR3gBMyzIUmUg_kkSO_EU4LqgkdA";
                feedArrayList.setPhotos(photosss);
            }
            return feedArrayList;
        }

    }

}
