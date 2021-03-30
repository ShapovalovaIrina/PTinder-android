package com.trkpo.ptinder.IT;

import android.os.Build;
import android.util.Log;

import androidx.test.runner.AndroidJUnit4;

import com.trkpo.ptinder.HTTP.GetRequest;
import com.trkpo.ptinder.HTTP.PostRequestParams;
import com.trkpo.ptinder.HTTP.PutRequest;
import com.trkpo.ptinder.pojo.PetInfo;
import com.trkpo.ptinder.utils.PetInfoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.trkpo.ptinder.config.Constants.PETS_PATH;
import static com.trkpo.ptinder.config.Constants.SEARCH_PATH;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class PetsScenariosTest {
    private TestUtils testUtils;

    @Before
    public void setUp() throws Exception {
        testUtils = new TestUtils();
    }

    @After
    public void clean() {
        testUtils.deleteAllPets();
        testUtils.deleteTestUser();
    }

    @Test
    public void testUserCanAddNewPetScenario() {
        testUtils.addUserWithGoogleId("1");
        testUtils.addPetForUser("1");
        List<PetInfo> pets = testUtils.getPetsForUser("1");
        assertEquals("Barsik", pets.get(0).getName());
        assertEquals("1", pets.get(0).getOwnerId());
    }

    @Test
    public void testUserCanUpdateInfoAboutPetScenario() {
        testUtils.addUserWithGoogleId("1");
        testUtils.addPetForUser("1");
        Long petId = testUtils.getPetsForUser("1").get(0).getId();
        JSONObject requestObject = PetInfoUtils.setPetToJSON(
                "NotBarsik",
                "5",
                "MALE",
                "КОТ",
                "",
                "Walking",
                "",
                null,
                "1"
        );
        final String requestBody = requestObject.toString();

        String url = PETS_PATH + "/" + petId;
        try {
            String response = new PutRequest().execute(new PostRequestParams(url, requestBody)).get();
            PetInfo updatedPet = testUtils.getPetsForUser("1").get(0);
            assertEquals("Barsik", updatedPet.getName());
            assertEquals("3 года", updatedPet.getAge());

        } catch (ExecutionException | InterruptedException | IllegalStateException error) {
            Log.e("VOLLEY", "Making put request (update pet): error - " + error.toString());
        }
    }

    @Test
    public void testCanPerformSearchForPetsScenario() {
        testUtils.addUserWithGoogleId("1");
        testUtils.addUserWithGoogleId("2");
        testUtils.addUserWithGoogleId("3");
        testUtils.addPetForUser("1");
        testUtils.addPetForUser("2");
        testUtils.addPetForUser("3");
        String address = "Peter";
        String gender = "MALE";
        String purpose = PetInfoUtils.formatPurposeToEnum("-");
        String type = "КОТ";

        String url = SEARCH_PATH
                + "?" + "address=" + address
                + "&" + "gender=" + gender
                + "&" + "purpose=" + purpose
                + "&" + "type=" + type
                + "&" + "minAge=" + "0"
                + "&" + "maxAge=" + "3";

        try {
            String response = new GetRequest().execute(url).get();
            System.out.println(response);
            JSONArray jArray = new JSONArray(response);
            assertEquals(3, jArray.length());
            assertEquals("Barsik", jArray.getJSONObject(0).getString("Name"));
            assertEquals("1", jArray.getJSONObject(0).getJSONObject("owner").getString("googleId"));
            assertEquals("Barsik", jArray.getJSONObject(1).getString("Name"));
            assertEquals("2", jArray.getJSONObject(1).getJSONObject("owner").getString("googleId"));
            assertEquals("Barsik", jArray.getJSONObject(2).getString("Name"));
            assertEquals("3", jArray.getJSONObject(2).getJSONObject("owner").getString("googleId"));

        } catch (ExecutionException | InterruptedException | JSONException error) {
            error.printStackTrace();
        }
    }

}
