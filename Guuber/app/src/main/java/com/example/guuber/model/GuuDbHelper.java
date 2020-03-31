package com.example.guuber.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GuuDbHelper {
    private static FirebaseFirestore db;
    private static CollectionReference requests;
    private static CollectionReference users;
    private static DocumentReference profile;
    public static User user;
    public static String rider;
    public static boolean notify;
    public static Map<String, Object> Request = new HashMap<>();
    public static Vehicle car = new Vehicle();
    public static ArrayList<Map<String, Object>> reqList = new ArrayList<Map<String, Object>>();
    public static String offerer;
    public static String offerStat = "none";

    public static String arrivee;
    public static String arriver;
    public static String arrivalStatus = "false";
    public static String cancelee;
    public static String canceler;
    public static String canceled = "false";
    public static Map<String,Object> profileInformation;



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
        Thread.sleep(1000);
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
     */
    public synchronized void setUser(String phone, String email, String first, String last, String uname, Integer rider, Integer posRating, Integer negRating) {
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
    public synchronized void checkEmail(User newUser){
        Map<String,Object> user = new HashMap<>();
        user.put("firstName",newUser.getFirstName());
        user.put("lastName",newUser.getLastName());
        user.put("email",newUser.getEmail());
        user.put("username",newUser.getUsername());
        user.put("phoneNumber",newUser.getPhoneNumber());
        //user.put("uid",newUser.getUid());
        user.put("rider",newUser.getRider());

        user.put("posRating", newUser.getPosRating());
        user.put("negRating", newUser.getNegRating());
        //user.put("balance", newUser.getBalance());
        //user.put("transactions", newUser.getTransHistory());


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
    public synchronized void createUser(Map<String,Object> info,User newUser){
        users.document(newUser.getEmail()).set(info);
    }

    /**
     * Helper function
     * set the profile of a user
     * @param email - email of the user
     */
    public synchronized void setProfile(String email){
        this.profile = users.document(email); //this thread is running while all other methods are finishing and causing crashes everywhere

    }

    /**
     * Deletes the user from the database
     * @param email - email of the user
     * */
    public synchronized void deleteUser(String email){
        setProfile(email);
        profile.delete();
    }

    /**
     * Updates the username of the user
     * @param email - the user that want to update their name
     * @param name - the new name to display
     */
    public synchronized void updateUsername(String email,String name){
        users.document(email).update("username",name);
    }

    /**
     * Updates the phonenumber of the user
     * @param email - the user that wants to update their number
     * @param number - the new contact number*/
    public synchronized void updatePhoneNumber(String email,String number){
        users.document(email).update("phoneNumber",number);
    }

    // Every time you update either of the ratings it automatically increments
    /**
     * automatically increments the positive rating of the user
     * @param email - the email of the user
     */
    public synchronized void updatePosRating(String email){
        users.document(email).update("posRating", FieldValue.increment(1));
    }

    /**
     * automatically increments the negative rating of the user
     * @param email - the email of the user
     */
    public synchronized void updateNegRating(String email){
        users.document(email).update("negRating", FieldValue.increment(1));
    }

    /**************
    public synchronized void  updateProfileAll(User user) throws InterruptedException {
        String email = user.getEmail();
        //User oldUser = getUser(user.getEmail());
        //instead!!!!! just use the user the is passed in, because that is equivalent to users information who is currentley using the app
        if (!oldUser.getPhoneNumber().equals(user.getPhoneNumber())){
            updatePhoneNumber(email, user.getPhoneNumber());
        }
        if (oldUser.getNegRating()!=user.getNegRating()){
            updateNegRating(email);
        }
        if (oldUser.getPosRating()!=user.getPosRating()){
            updateNegRating(email);
        }
        if (oldUser.getUsername().equals(user.getUsername())){
            updateUsername(email, user.getEmail());
        }

    }
     ************************/

    /**
     * returns an ArrayList of profile information
     * @param email
     * @return
     */
    public synchronized Map<String,Object> getProfileAll(String email) {
        setProfile(email);
        profile.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                profileInformation = documentSnapshot.getData();
//                String t = documentSnapshot.get("phoneNumber").toString();
//                profileInformation.add(t);
//                String i = documentSnapshot.get("email").toString();
//                profileInformation.add(i);
//                String n = documentSnapshot.get("firstName").toString();
//                profileInformation.add(n);
//                String a = documentSnapshot.get("lastName").toString();
//                profileInformation.add(a);
//                String s = documentSnapshot.get("username").toString();
//                profileInformation.add(s);
//                String h = documentSnapshot.get("posRating").toString();
//                profileInformation.add(h);
//                String e = documentSnapshot.get("negRating").toString();
//                profileInformation.add(e);
            }
        });
        return profileInformation;
    }









    /**
     * Updates users balance (also updates transactions by appending amount to be added to balance to the transactions list)
     * @param email - the email of the user
     */
    public synchronized void updateBalance(String email, Double amount)
    {
        users.document(email).update("balance", FieldValue.increment(amount));
        users.document(email).update("transactions", FieldValue.arrayUnion(amount)); // currently treats array as a kind of key value pair so transactions with the same amount will not be appended, I am trying to fix this...
    }



    /**
     * Creates and stores the request into the database
     * @param rider - the person making the request
     * @param tip - the extra amount they are willing to pay
     * @param oriLat - Latitudinal coordinate of original place to be pickup
     * @param oriLng - Longitudinal coordinate of original place to be pickup
     * @param desLat - Latitudinal coordinate of the destination
     * @param desLng - Latitudinal coordinate of the destination
     * @param tripCost - the cost of the trip
     *  >WORKS
     */
    public synchronized void makeReq(User rider, Double tip, double oriLat, double oriLng, double desLat, double desLng, String tripCost){
        setProfile(rider.getEmail());
        Map<String,Object> details = new HashMap<>();
        details.put("reqTip",tip);
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
     *  >WORKS
     */
    public synchronized void cancelRequest(User rider) {
        setProfile(rider.getEmail());

        profile.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Map<String, Object> delete = new HashMap<>();
                        delete.put("reqTip", FieldValue.delete());
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
     * @param oriLat - Latitudinal coordinate of original place to be pickup
     * @param oriLng - Longitudinal coordinate of original place to be pickup
     * @param desLat - Latitudinal coordinate of the destination
     * @param desLng - Latitudinal coordinate of the destination
     * @param tripCost - the cost of the trip
     */
    public synchronized void setRequest(String email, Object tip , Object oriLat, Object oriLng, Object desLat, Object desLng, String tripCost){
        this.Request.put("reqTip", tip);
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
    public synchronized Map<String,Object> getRiderRequest(User rider) throws InterruptedException {
        setProfile(rider.getEmail());
        profile.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
               setRequest(documentSnapshot.get("email").toString(), documentSnapshot.get("reqTip"),
                       documentSnapshot.get("oriLat"), documentSnapshot.get("oriLng"), documentSnapshot.get("desLat"),
                       documentSnapshot.get("desLng"), documentSnapshot.get("tripCost").toString());
            }
        });
        TimeUnit.SECONDS.sleep(5);
        return Request;
    }

    //gets the first document of the driver request
    /**
     * Get the information of the driver's current active request they have
     * @param  driver - the driver with a request
     * @return - the details of the request in as a Map<String,Object> format </String,Object>
     * */
    public synchronized Map<String,Object> getDriverActiveReq(User driver){
        setProfile(driver.getEmail());
       Task<QuerySnapshot> activeReq =  profile.collection("driveRequest").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
           @Override
           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().isEmpty()) {
                        DocumentSnapshot activeReq = task.getResult().getDocuments().get(0);
                        setRequest(activeReq.getId(), activeReq.get("reqTip").toString(),
                                activeReq.get("oriLat"), activeReq.get("oriLng"),
                                activeReq.get("desLat"), activeReq.get("desLng"),
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
    public synchronized ArrayList<Map<String,Object>> getReqList(){
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
    public synchronized void updateReqList(String email,Map<String,Object> reqDetails){
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
        //Thread.sleep(1000);
        return offerer;
    }

    /**
     * Helper function for seeOffer
     * sets the offerer email
     */
    public synchronized void setOfferer(String driver){
        offerer = driver;
    }

    /**
     * Let the rider decline the offer from the driver
     * @param rider - the person who declines the offer
     */
    public synchronized void declineOffer(User rider){
        setProfile(rider.getEmail());
        profile.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                offerer = documentSnapshot.get("rideOfferFrom").toString();
                setProfile(offerer);
                profile.update("offerStatus", "declined");
            }
        });
        setProfile(offerer);
        profile.update("offerStatus","declined");
    }

    /**
     * the rider accepting the offer they recieved
     * @param rider - the rider who accept the offer
     */
    public synchronized void acceptOffer(User rider){
        setProfile(rider.getEmail());
        profile.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                arrivee = rider.getEmail();
                setProfile(arrivee);
                profile.update("arrived","false"); //rider now has a field
                profile.update("canceled","false"); //rider now has a field

                offerer = documentSnapshot.get("rideOfferFrom").toString();
                setProfile(offerer);
                profile.update("offerStatus","accepted");
                profile.update("arrived","false"); //driver now has a field
                profile.update("canceled","false"); //rider now has a field
            }
        });
    }

    /**
     * get the status of a cancellation
     * @param driverEmail the drivers email
     * @return either false or true
     */
    public synchronized String getCancellationStatus(String driverEmail){
        setProfile(driverEmail);
        profile.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                canceled = documentSnapshot.get("cancelStatus").toString();
            }
        });
        return canceled;
    }

    /**
     * get the status of a cancelation
     * @param riderEmail the drivers email
     * @return either false or true
     */
    public synchronized String setCancellationStatus(String riderEmail, String driverEmail){
        setProfile(riderEmail);
        profile.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                canceler = riderEmail;
                setProfile(canceler);
                profile.update("canceled","true");

                cancelee = driverEmail;
                setProfile(cancelee);
                profile.update("canceled","true"); //driver now has a field
            }
        });
        return canceled;
    }


    /**
     * function to set the status of the arrival. Driver sets to true upon arrival
     * @param email is the drivers email
     * WORKS >
     */
    public synchronized void setArrival(String email){
        setProfile(email);
        profile.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                arriver = email;
                setProfile(arriver);
                profile.update("arrived","true");

                arrivee = documentSnapshot.get("offerTo").toString();
                setProfile(arrivee);
                profile.update("arrived","true"); //driver now has a field
            }
        });
    }

    public synchronized  void notifyRider(User driver){

}
    /**
     * function to get the status of the arrival
     * @param email is the riders email
     *  WORKS >
     */
    public synchronized String getArrival(String email){
        setProfile(email);
        users.document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                arrivalStatus = documentSnapshot.get("arrived").toString();
                if (arrivalStatus.equals("true")){
                    android.util.Log.i("ARRIVAL STATUS =", "true");
                }else if (arrivalStatus.equals("false")){
                    android.util.Log.i("ARRIVAL STATUS =", "false");
                }
            }
        });
        return arrivalStatus;

    }


    public synchronized String returnMe(String x){
        return x;
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
    public synchronized String checkOfferStatus(User driver) throws InterruptedException {
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
        profile.update("offerStatus",FieldValue.delete());
        profile.collection("driveRequest").document(rider.getEmail()).set(reqDetails);

    }

    /**
     * once the transaction is made the request is complete, this removes all the info about request
     * @param driver - the driver who completed the request
     * @param rider - the rider whos request was fulfilled
     */
    public void completedRequest(User driver,User rider){
        setProfile(driver.getEmail());
        profile.collection("driveRequest").document(rider.getEmail()).delete();
        Map<String, Object> delete = new HashMap<>();
        delete.put("reqTip", FieldValue.delete());
        delete.put("oriLat", FieldValue.delete());
        delete.put("oriLng", FieldValue.delete());
        delete.put("desLat", FieldValue.delete());
        delete.put("desLng", FieldValue.delete());
        delete.put("tripCost",FieldValue.delete());
        delete.put("reqDriver",FieldValue.delete());
        setProfile(rider.getEmail());
        profile.update(delete);
    }
    /**
     * Checks if driver has arrived to riders requested location
     * returns true if driverLocation == riderLocation
     *
     * @param rider - the rider with the request
     *
     * @param dLat - the current Latitude of the driver (str)
     * @param dLng - the current Longitude of the driver (str)
     *
     */
    public synchronized  Boolean driverArrive(User rider, String dLat, String dLng) {
        // rider coordinates
        final Double[] rLat = new Double[1];
        final Double[] rLng = new Double[1];


        setProfile(rider.getEmail());
        DocumentReference riderRef = db.collection("Users").document(rider.getEmail());
        riderRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                assert document != null;
                rLat[0] = (Double) document.getDouble("oriLat");
                rLng[0] = (Double) document.getDouble("oriLng");

            }
        });


        /** Convert Coordinates to doubles
         If one of the coordinates is a word or anything except numbers this function cannot work and returns a false
        try{
            Double testCurrentLat = new Double(currentLat).doubleValue();
            Double testCurrentLng = new Double(currentLng).doubleValue();
        }catch (Exception e) {return false;}
        */


        // Cut off after 5th decimal, so when you compare the drivers coordinates to the users, they don't have to be EXACTLY on them
        DecimalFormat df = new DecimalFormat("#.#####");
        String ddLat = df.format(Double.parseDouble(dLat));
        String ddLng = df.format(Double.parseDouble(dLng));

        String rrLat = df.format(rLat[0]);
        String rrLng = df.format(rLng[0]);


        if (rrLat == ddLat && rrLng == ddLng) {
            return true; // driver has arrived to riders location
        }
        else {
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
    public synchronized void addVehicle(User user, Vehicle car){
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
    public synchronized Vehicle getCarDetail(User driver){
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
    public synchronized void setVehicle(String make,String model,String color,String driver){
        this.car.setMake(make);
        this.car.setModel(model);
        this.car.setColor(color);
        this.car.setReg(driver);

    }


    


}

