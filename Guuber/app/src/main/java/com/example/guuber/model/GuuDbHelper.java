package com.example.guuber.model;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class GuuDbHelper {
    private static FirebaseFirestore db;
    private static CollectionReference requests;
    private static CollectionReference users;
    private static DocumentReference profile;
    public static User user;
    public static Map<String, Object> Request = new HashMap<>();
    public static Vehicle car = new Vehicle();
    public static ArrayList<Map<String, Object>> reqList = new ArrayList<Map<String, Object>>();
    public static String offerer;
    public static String offerStat = "none";


    //public static Wallet wall;
    //private static CollectionReference wallet;
    //public static Map<String,Object> Wallet = new HashMap<>();
    //public static ArrayList<Map<String, String>> walletList = new ArrayList<Map<String, String>>();

    /**
     * on create
     *
     * @param db - the instance of a FirebaseFirestone
     */
    public GuuDbHelper(FirebaseFirestore db) {
        this.db = db.getInstance();
        this.users = this.db.collection("Users");
        this.requests = this.db.collection("requests");
        this.user = new User();
    }


    /**
     * Helper function for getUser
     * Finds if the document of the email
     *
     * @param email - the email of the document to find
     */
    public synchronized void findUser(String email) throws InterruptedException {
        users.document(email).get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Log.d("user", "found user");
                    setUser(documentSnapshot.get("phoneNumber").toString(),
                            documentSnapshot.get("email").toString(),
                            documentSnapshot.get("firstName").toString(),
                            documentSnapshot.get("lastName").toString(),
                            documentSnapshot.get("username").toString(),
                            (int) (long) documentSnapshot.get("rider"),
                            (int) (long) documentSnapshot.get("posRating"),
                            (int) (long) documentSnapshot.get("negRating")

                            //,documentSnapshot.getDouble("balance"),
                            //(ArrayList<Double>) documentSnapshot.get("transactions")
                    );
                }
                else {
                    Log.d("user", "user does not exist");
                }
            }
        });
    }



    /**
     * Helper function
     * set the user info for getUser
     *
     * @param phone        - user's phonenumber
     * @param email        - user's email
     * @param first        - user's first name
     * @param last         - user's last name
     * @param uname        - user's username
     * @param rider        - whether if the user is a rider of driver
     * @param posRating    - number of ratings that are positive
     * @param negRating    - number of ratings that are negative
     * @param balance      - Amount in users wallet
     * @param transactions - list of transactions(changes to their balance) that the user incurred

     */
    public void setUser(String phone, String email, String first, String last, String uname, Integer rider, Integer posRating, Integer negRating) {
            this.user.setEmail(email);
            this.user.setPhoneNumber(phone);
            this.user.setFirstName(first);
            this.user.setLastName(last);
            this.user.setUsername(uname);
            this.user.setRider(rider);

            this.user.setPosRating(posRating);
            this.user.setNegRating(negRating);

            //this.user.setBalance(balance);
            //this.user.setTransHistory(transactions);
    }

    /**
     * Gets the information under the person's email from the database
     *
     * @param email - the user's email
     * @return - the user under the email inputted
     */

    public synchronized User getUser (String email) throws InterruptedException {
        findUser(email);
        return user;
    }


    //NOTE: function should not be used since the users are already created through loginActivity
    /**
     * Creates the user's account if it does not exist
     * @param newUser - the user information
     *
     * */
    public void checkEmail(User newUser){
        Map<String,Object> user = new HashMap<>();
        user.put("firstName",newUser.getFirstName());
        user.put("lastName",newUser.getLastName());
        user.put("email",newUser.getEmail());
        user.put("username",newUser.getUsername());
        user.put("phoneNumber",newUser.getPhoneNumber());
//        user.put("uid",newUser.getUid());
        user.put("rider",newUser.getRider());

        user.put("posRating", newUser.getPosRating());
        user.put("negRating", newUser.getNegRating());

//        user.put("balance", newUser.getBalance());
//        user.put("transactions", newUser.getTransHistory());



        users.document(newUser.getEmail()).get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if( !documentSnapshot.exists()){
                    Log.d("checking email in db","Email does not exist");
                    createUser(user, newUser);
                }
                else{
                    Log.d("checking email in db","Email exist");
                }
            }
        });


    }
    /**
     * Helper function for checkEmail
     * creates a document in the database with the user's info
     */
    public void createUser(Map<String,Object> info,User newUser){
        users.document(newUser.getEmail()).set(info);
    }

    /**
     * Helper function
     * set the profile of a user
     * @param email - email of the user
     */
    public synchronized void setProfile(String email){
        this.profile = users.document(email);
    }

    /**
     * Deletes the user from the database
     * @param email - email of the user
     * */
    public void deleteUser(String email){
        setProfile(email);
        profile.delete();
    }

    /**
     * Updates the username of the user
     * @param email - the user that want to update their name
     * @param name - the new name to display
     */
    public void updateUsername(String email,String name){
        users.document(email).update("username",name);
    }
    /**
     * Updates the phonenumber of the user
     * @param email - the user that wants to update their number
     * @param number - the new contact number*/
    public void updatePhoneNumber(String email,String number){
        users.document(email).update("phoneNumber",number);
    }

    // Every time you update either of the ratings it automatically increments
    /**
     * automatically increments the positive rating of the user
     * @param email - the email of the user
     */
    public void updatePosRating(String email){
        users.document(email).update("posRating", FieldValue.increment(1));
    }

    /**
     * automatically increments the negative rating of the user
     * @param email - the email of the user
     */
    public void updateNegRating(String email){
        users.document(email).update("negRating", FieldValue.increment(1));
    }



    /**
     * Updates users balance (also updates transactions by appending amount to be added to balance to the transactions list)
     * @param email - the email of the user
     */
    public void updateBalance(String email, Double amount)
    {
        users.document(email).update("balance", FieldValue.increment(amount));
        users.document(email).update("transactions", FieldValue.arrayUnion(amount)); // currently treats array as a kind of key value pair so transactions with the same amount will not be appended, I am trying to fix this...
    }



    /**
     * Creates and stores the request into the database
     * @param rider - the person making the request
     * @param tip - the extra amount they are willing to pay
     * @param location - the pickup location
     * @param oriLat - Latitudinal coordinate of original place to be pickup
     * @param oriLng - Longitudinal coordinate of original place to be pickup
     * @param desLat - Latitudinal coordinate of the destination
     * @param desLng - Latitudinal coordinate of the destination
     * @param tripCost - the cost of the trip
     */

    public void makeReq(User rider, Double tip, String location, String oriLat, String oriLng, String desLat, String desLng, String tripCost){
        setProfile(rider.getEmail());
        Map<String,Object> details = new HashMap<>();
        details.put("reqTip",tip);
        details.put("reqLocation",location);
        details.put("oriLat",oriLat);
        details.put("oriLng",oriLng);
        details.put("desLat",desLat);
        details.put("desLng",desLng);
        details.put("tripCost",tripCost);

        //details.put("driverArrive", driverArrive = false);

        this.profile.update(details);

        this.requests.document(rider.getEmail()).set(details);

    }

    /**
     * Cancels the user's request
     * @param rider - rider who want to cancel their request
     */
    public void cancelRequest(User rider) {
        setProfile(rider.getEmail());

        profile.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Map<String, Object> delete = new HashMap<>();
                        delete.put("reqTip", FieldValue.delete());
                        delete.put("reqLocation", FieldValue.delete());
                        delete.put("oriLat", FieldValue.delete());
                        delete.put("oriLng", FieldValue.delete());
                        delete.put("desLat", FieldValue.delete());
                        delete.put("desLng", FieldValue.delete());
                        delete.put("tripCost",FieldValue.delete());
                        //checks if there is a driver for the request
                        if (doc.get("reqDriver") != null) {
                            delete.put("reqDriver", FieldValue.delete());
                            setProfile(doc.get("reqDriver").toString());
                            profile.collection("driveRequest").document(rider.getEmail()).delete();
                            setProfile(rider.getEmail());
                            profile.update(delete);
                        } else {
                            setProfile(rider.getEmail());
                            Map<String,Object> reqInfo = new HashMap<>();
                            reqInfo.put("reqTip", doc.get("reqTip"));
                            reqInfo.put("reqLocation",doc.get("reqLocation"));
                            reqInfo.put("oriLat",doc.get("oriLat"));
                            reqInfo.put("oriLng",doc.get("oriLng"));
                            reqInfo.put("desLat",doc.get("desLat"));
                            reqInfo.put("desLng",doc.get("desLng"));
                            reqInfo.put("email",doc.get("email"));
                            reqInfo.put("tripCost",doc.get("tripCost"));
                            profile.update(delete);
                            requests.document(rider.getEmail()).delete();
                            reqList.remove(reqInfo);


                        }
                    }
                }
            }
        });
    }
    /**
     * Helper function for getRiderRequest
     * Sets the request info to be retrieved
     * @param email - the email of the person
     * @param tip - extra amount the person offers
     * @param location - the destination
     * @param oriLat - Latitudinal coordinate of original place to be pickup
     * @param oriLng - Longitudinal coordinate of original place to be pickup
     * @param desLat - Latitudinal coordinate of the destination
     * @param desLng - Latitudinal coordinate of the destination
     * @param tripCost - the cost of the trip
     */
    public void setRequest(String email, Object tip ,String location, String oriLat,String oriLng,String desLat,String desLng,String tripCost){
        this.Request.put("reqTip", tip);
        this.Request.put("reqLocation",location);
        this.Request.put("oriLat",oriLat);
        this.Request.put("oriLng",oriLng);
        this.Request.put("desLat",desLat);
        this.Request.put("desLng",desLng);
        this.Request.put("email",email);
        this.Request.put("tripCost",tripCost);


    }

    /**
     * Gets the specific request of the rider specified
     * @param rider - the user who made the request
     * @return - the details of the request in as a Map<String,Object> format </String,Object>
     */
    public Map<String,Object> getRiderRequest(User rider) throws InterruptedException {

        setProfile(rider.getEmail());
        TimeUnit.SECONDS.sleep(5);
        profile.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
               setRequest(documentSnapshot.get("email").toString(), documentSnapshot.get("reqTip"),
                       documentSnapshot.get("reqLocation").toString(),documentSnapshot.get("oriLat").toString(),
                       documentSnapshot.get("oriLng").toString(),documentSnapshot.get("desLat").toString(),
                       documentSnapshot.get("desLng").toString(),documentSnapshot.get("tripCost").toString());
            }
        });

        return Request;
    }

    //gets the first document of the driver request
    /**
     * Get the information of the driver's current active request they have
     * @param  driver - the driver with a request
     * @return - the details of the request in as a Map<String,Object> format </String,Object>
     * */
    public Map<String,Object> getDriverActiveReq(User driver){
        setProfile(driver.getEmail());
       Task<QuerySnapshot> activeReq =  profile.collection("driveRequest").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
           @Override
           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().isEmpty()) {
                        DocumentSnapshot activeReq = task.getResult().getDocuments().get(0);
                        setRequest(activeReq.getId(), activeReq.get("reqTip").toString(), activeReq.get("reqLocation").toString(),
                                activeReq.get("oriLat").toString(), activeReq.get("oriLng").toString(),
                                activeReq.get("desLat").toString(), activeReq.get("desLng").toString(),
                                activeReq.get("tripCost").toString());
                    }
                    else{
                        Request.clear();
                    }
                }

           }
       });
       return Request;
    }

    /**
     * Gets a list of current request that riders post
     * @return - an ArrayList<Map<String,Object>> </String,Object> of request that need a driver
     * */
    public ArrayList<Map<String,Object>> getReqList(){
        requests.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Log.d("requestList",document.getId());
                        updateReqList(document.getId(),document.getData());
                    }
                }
                else{
                    Log.d("requestList","failed");
                }
            }
        });
        return this.reqList;
    }
    /**
     * Helper function
     * add the user email to the request detail
     */
    public void updateReqList(String email,Map<String,Object> reqDetails){
        reqDetails.put("email", email);
        if(!reqList.contains(reqDetails)) {
            this.reqList.add(reqDetails);
        }
    }

    /**
     * Driver can offer a rider a ride but pending on riders approval
     * @param driver - the driver offering a rider
     * @param rider - the driver offering the ride to
     */
    public synchronized void offerRide(User driver,User rider){
        setProfile(driver.getEmail());
        profile.update("offerStatus","pending");
        profile.update("offerTo",rider.getEmail());
        setProfile(rider.getEmail());
        profile.update("rideOfferFrom",driver.getEmail());
    }

    /**
     * lets the rider see if they have am offer for a ride
     * @param rider - the rider who may have someone offering a rider to them
     * @return - the email of the driver who is offering them a ride in a string
     */
    public synchronized String seeOffer(User rider) throws InterruptedException {
        setProfile(rider.getEmail());
        profile.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.get("rideOfferFrom")!= null){
                        setOfferer(documentSnapshot.get("rideOfferFrom").toString());
                    }
                    else{
                        offerer = null;
                    }
                }
            }
        });
        Thread.sleep(1000);
        return offerer;
    }

    /**
     * Helper function for seeOffer
     * sets the offerer email
     */
    public void setOfferer(String driver){
        offerer = driver;
    }

    /**
     * Let the rider decline the offer from the driver
     * @param rider - the person who declines the offer
     * @param driver - the person who's offer is declined
     */
    public void declineOffer(User rider,User driver){
        setProfile(rider.getEmail());
        profile.update("rideOfferFrom",FieldValue.delete());
        setProfile(driver.getEmail());
        profile.update("offerTo",FieldValue.delete());
        profile.update("offerStatus","declined");
    }
    /**
     * the rider accepting the offer they recieved
     * @param rider - the rider who accept the offer
     */
    public void acceptOffer(User rider){
        setProfile(rider.getEmail());
        profile.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                offerer = documentSnapshot.get("rideOfferFrom").toString();
                setProfile(offerer);
                profile.update("offerStatus","accepted");
            }
        });
        setProfile(offerer);
        profile.update("offerStatus","accepted");
    }

    /**
     * Allows the driver to see the status of their offer to the rider
     * @driver - the driver who offer a ride for a rider
     * @return - the status of the offer either being:
     *              pending: waiting for response
     *              accepted: the offer has been accepted
     *              declined: the rider declined the offer
     *              none: the driver did not offer a ride to anyone
     */
    public String checkOfferStatus(User driver) throws InterruptedException {
        setProfile(driver.getEmail());

        profile.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.get("offerStatus") != null){
                    offerStat = documentSnapshot.get("offerStatus").toString();
                } else{
                    offerStat = "none"; }
                if(offerStat != null){
                    if(offerStat.equals("declined")){
                        profile.update("offerStatus",FieldValue.delete());
                    }
                }
            }
        });

        return offerStat;
    }


    /**
     * Stores details between driver and rider when a request is accepted
     * @param rider - the rider with the request and accepts driver
     * @param driver - the driver who takes the request
     */
    public synchronized void reqAccepted(User rider, User driver) throws InterruptedException {

        setProfile(rider.getEmail());
        profile.update("rideOfferFrom",FieldValue.delete());
        profile.update("reqDriver",driver.getEmail());
        Map<String,Object> reqDetails = getRiderRequest(rider);
        reqList.remove(reqDetails);
        requests.document(rider.getEmail()).delete();
        setProfile(driver.getEmail());
        profile.update("offerTo",FieldValue.delete());
        profile.update("offerStatus",FieldValue.delete());
        profile.collection("driveRequest").document(rider.getEmail()).set(reqDetails);

    }

    /**
     * Checks if driver has arrived to riders requested location
     * returns true if driverLocation == riderLocation
     *
     * @param rider - the rider with the request and accepts driver
     *
     * @param currentLat - the current Latitude of the driver
     * @param currentLng - the current Longitude of the driver
     *
     */
    public Boolean driverArrive(User rider, String currentLat, String currentLng)
    {
        setProfile(rider.getEmail());
        DocumentReference ref = db.collection("requests").document(rider.getEmail());

        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                String Lat = (String) document.getString("oriLat");
                String Lng = (String) document.getString("oriLng");

            }
        });

        // Convert Coordinates to doubles
        Double newLat = new Double(Lat).doubleValue();
        Double newLng = new Double(Lng).doubleValue();
        Double newCurrentLat = new Double(currentLat).doubleValue();
        Double newCurrentLng = new Double(currentLng).doubleValue();

        // Cut off after 5th decimal, so when you compare the drivers coordinates to the users, they don't have to be EXACTLY on them
        DecimalFormat df = new DecimalFormat("#.#####");
        Double newerLat = df.format(newLat);
        Double newerLng = df.format(newLng);

        Double newerCurrentLat = df.format(dcurrentLat);
        Double newerCurrentLng = df.format(dcurrentLng);

        if (newerLat == newerCurrentLat && newerLng == newerCurrentLng)
        {
            return true; // driver has arrived to riders location
        }
        else
        {
            return false; // driver is not at rider location
        }

        //Map<String,Object> location;
        //Map<String,Object> location = profile.collection("requests").document(rider.getEmail()).;
        //String Lat = location.get("oriLat"));
        //String Lng = location.get("oriLng"));
    }


    /**
     * Adds or updates the current vehicle to the users profile
     * @param user - the user who has the registered car
     * @param car - the car to be register in the database
     */
    public void addVehicle(User user, Vehicle car){
        setProfile(user.getEmail());
        profile.update("vehMake",car.getMake());
        profile.update("vehModel",car.getModel());
        profile.update("vehColor",car.getColor());
    }

    /**
     * Gets the information of the vehicle the user has
     * @param driver - The person who own the vehicle
     * @return - the details of the vehicle the user owns
     */
    public Vehicle getCarDetail(User driver){
        setProfile(driver.getEmail());
        profile.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    Log.d("doc","found document");
                    if(documentSnapshot.get("vehMake") != null){
                        Log.d("carDetails","car exist");
                        setVehicle(documentSnapshot.get("vehMake").toString(),documentSnapshot.get("vehModel").toString(),
                                documentSnapshot.get("vehColor").toString(),documentSnapshot.getId());
                    }
                    else{
                        Log.d("carDetails","car does not exist");
                    }

                }
                else{
                    Log.d("doc","Cannot find document");
                }
            }
        });
        return car;
    }

    /**
     * Helper function
     * sets the vehicle info for getCarDetail to user
     * @param make - the maker of the car
     * @param model - the model of the car
     * @param color - the color of the car
     * @param driver - the car that the driver is registered to
     */
    public void setVehicle(String make,String model,String color,String driver){
        this.car.setMake(make);
        this.car.setModel(model);
        this.car.setColor(color);
        this.car.setReg(driver);

    }


    


}

