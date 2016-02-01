package com.example.william.myapplication;

import android.util.Log;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Jenjobs {
    public static final String DATABASE_NAME = "jenjobs";
    public static final int DATABASE_VERSION = 1;

    public static final String AUTH_URL = "http://api.jenjobs.com/oauth2/token";
    public static final String PROFILE_URL = "http://api.jenjobs.com/jobseeker/profile";
    public static final String APPLICATION_URL = "http://api.jenjobs.com/jobseeker/application";
    public static final String APPLICATION_STATUS_URL = "http://api.jenjobs.com/jobseeker/application-status";
    public static final String WORK_EXPERIENCE_URL = "http://api.jenjobs.com/jobseeker/work-experience";
    public static final String EDUCATION_URL = "http://api.jenjobs.com/jobseeker/qualification";
    public static final String JOB_PREFERENCE_URL = "http://api.jenjobs.com/jobseeker/job-preference";
    public static final String SKILL_URL = "http://api.jenjobs.com/jobseeker/skill";
    public static final String LANGUAGE_URL = "http://api.jenjobs.com/jobseeker/language";
    public static final String BOOKMARK_URL = "http://api.jenjobs.com/jobseeker/bookmark";
    public static final String SUBSCRIPTION_URL = "http://api.jenjobs.com/jobseeker/subscription";
    public static final String JOB_SPEC_URL = "http://api.jenjobs.com/parameters/job-spec";
    public static final String JOB_DETAILS = "http://api.jenjobs.com/jobs/search";
    public static final String ATTACH_RESUME = "http://api.jenjobs.com/jobseeker/attachment";
    public static final String REGISTRATION_URL = "http://api.jenjobs.com/register/jobseeker";
    public static final String FORGOT_PASSWORD_URL = "http://api.jenjobs.com/forgot-password/jobseeker";
    public static final String INVITATION_URL = "http://api.jenjobs.com/employer/invitation";
    public static final String SEARCH_PROFILE = "http://api.jenjobs.com/jobseeker/jobmatcher-profile";

    public static HashMap getJobType(){
        HashMap<Integer, String> a = new HashMap<>();
        a.put(1, "Permanent");
        a.put(2, "Contract");
        a.put(3, "Part-Time/Temporary");
        a.put(4, "Internship");
        return a;
    }

    public static HashMap getPositionLevel(){
        HashMap<Integer, String> a = new HashMap<>();
        a.put(1, "Non-Executive");
        a.put(2, "Executive");
        a.put(3, "Management");
        a.put(4, "Senior Management");
        a.put(5, "Entry Level");
        a.put(6, "Senior Executive");
        return a;
    }

    public static HashMap getCurrency(){
        HashMap<Integer, String> a = new HashMap<>();
        a.put(1, "AUD");
        a.put(2, "THB");
        a.put(3, "EUR");
        a.put(4, "HKD");
        a.put(5, "PHP");
        a.put(6, "MYR");
        a.put(7, "IDR");
        a.put(8, "INR");
        a.put(9, "SGD");
        a.put(10, "USD");
        a.put(11, "CNY");
        a.put(12, "JPY");
        a.put(13, "GBP");
        a.put(14, "VND");
        a.put(15, "NZD");
        a.put(16, "TWD");
        a.put(17, "CAD");
        a.put(18, "CHF");
        return a;
    }

    /*
    "49": "Accounting Firms",
    "50": "Advertising / PR / Events",
    "51": "Aerospace / Aviation",
    "52": "Agriculture / Plantation",
    "53": "Automotive",
    "54": "Banking / Finance / Investment",
    "55": "Bio-Technology / Life Sciences",
    "56": "Broadcasting / Publishing",
    "57": "Call Centre",
    "58": "Construction / Building / Architecture",
    "59": "Consulting",
    "60": "Consumer Products",
    "61": "Education / Training",
    "62": "Electrical & Electronics",
    "63": "Engineering",
    "64": "Entertainment",
    "65": "Food & Beverage",
    "66": "Freight / Shipping",
    "67": "Government / NGO / Non Profit",
    "68": "Health / Medical Care / Pharmaceutical",
    "69": "Hotel / Resorts / Travel",
    "70": "Human Resource / Recruitment",
    "71": "Industrial Products",
    "72": "Information Technology",
    "73": "Insurance",
    "74": "Legal Services",
    "75": "Logistics / Warehousing",
    "76": "Manufacturing / Production",
    "77": "Oil & Gas",
    "78": "Others",
    "79": "Property / Real Estate",
    "80": "Research & Development",
    "81": "Retail / Trading Distribution",
    "82": "Science & Technology",
    "83": "Stock Broking / Securities",
    "84": "Telecommunications",
    "86": "Transportation",
    "87": "Utilities"
    */
    public static HashMap getIndustry(){
        HashMap<Integer, String> a = new HashMap<>();
        a.put(49, "Accounting Firms");
        a.put(50, "Advertising / PR / Events");
        a.put(51, "Aerospace / Aviation");
        a.put(52, "Agriculture / Plantation");
        a.put(53, "Automotive");
        a.put(54, "Banking / Finance / Investment");
        a.put(55, "Bio-Technology / Life Sciences");
        a.put(56, "Broadcasting / Publishing");
        a.put(57, "Call Centre");
        a.put(58, "Construction / Building / Architecture");
        a.put(59, "Consulting");
        a.put(60, "Consumer Products");
        a.put(61, "Education / Training");
        a.put(62, "Electrical & Electronics");
        a.put(63, "Engineering");
        a.put(64, "Entertainment");
        a.put(65, "Food & Beverage");
        a.put(66, "Freight / Shipping");
        a.put(67, "Government / NGO / Non Profit");
        a.put(68, "Health / Medical Care / Pharmaceutical");
        a.put(69, "Hotel / Resorts / Travel");
        a.put(70, "Human Resource / Recruitment");
        a.put(71, "Industrial Products");
        a.put(72, "Information Technology");
        a.put(73, "Insurance");
        a.put(74, "Legal Services");
        a.put(75, "Logistics / Warehousing");
        a.put(76, "Manufacturing / Production");
        a.put(77, "Oil & Gas");
        a.put(78, "Others");
        a.put(79, "Property / Real Estate");
        a.put(80, "Research & Development");
        a.put(81, "Retail / Trading Distribution");
        a.put(82, "Science & Technology");
        a.put(83, "Stock Broking / Securities");
        a.put(84, "Telecommunications");
        //a.put(85, "");
        a.put(86, "Transportation");
        a.put(87, "Utilities");
        return a;
    }

    /*
    "jobseekingStatus": {
        "2": "Actively looking",
        "3": "Open to good offer",
        "4": "Satisfied with current job"
    }
    */
    public static HashMap getJobSeekingStatus(){
        HashMap<Integer, String> a = new HashMap<>();
        a.put(2, "Actively looking");
        a.put(3, "Open to good offer");
        a.put(4, "Satisfied with current job");
        return a;
    }

    /*
    "language": {
        "2": "Bahasa Malaysia",
        "3": "English",
        "4": "French",
        "5": "German",
        "6": "Italian",
        "7": "Japanese",
        "8": "Korean",
        "9": "Chinese",
        "10": "Spanish",
        "11": "Tamil",
        "12": "Filipino",
        "13": "Portuguese",
        "14": "Bengali",
        "15": "Arabic",
        "16": "Russian",
        "17": "Hindi",
        "18": "Thai",
        "19": "Vietnamese",
        "20": "Dutch"
    }
    */
    public static HashMap getLanguage(){
        HashMap<Integer, String> a = new HashMap<>();
        a.put(2, "Bahasa Melayu");
        a.put(3, "English");
        a.put(4, "French");
        a.put(5, "German");
        a.put(6, "Italian");
        a.put(7, "Japanese");
        a.put(8, "Korean");
        a.put(9, "Chinese");
        a.put(10, "Spanish");
        a.put(11, "Tamil");
        a.put(12, "Filipino");
        a.put(13, "Portuguese");
        a.put(14, "Bengali");
        a.put(15, "Arabic");
        a.put(16, "Russian");
        a.put(17, "Hindi");
        a.put(18, "Thai");
        a.put(19, "Vietnamese");
        a.put(20, "Dutch");
        return a;
    }

    /*
    "languageLevel": {
        "1": "---",
        "2": "Basic",
        "3": "Good",
        "4": "Excellent"
    }
    */
    public static HashMap getLanguageLevel(){
        HashMap<Integer, String> a = new HashMap<>();
        a.put(1, "---");
        a.put(2, "Basic");
        a.put(3, "Good");
        a.put(4, "Excellent");
        return a;
    }

    /*
    "educationLevel": {
        "1": "Primary School/UPSR",
        "2": "Secondary School/SRP/PMR",
        "3": "Secondary School/\"O\" Level/SPM",
        "4": "Higher Secondary/Pre-U/\"A\" Level/STPM",
        "5": "Professional Certificate/Qualification",
        "6": "Diploma",
        "7": "Advanced/Higher Diploma",
        "8": "Bachelor's Degree",
        "9": "Post Graduate Diploma",
        "10": "Professional Degree",
        "11": "Master's Degree",
        "12": "Doctorate Degree"
    }
    */
    public static HashMap getEducationLevel(){
        HashMap<Integer, String> a = new HashMap<>();
        a.put(1, "Primary School/UPSR");
        a.put(2, "Secondary School/SRP/PMR");
        a.put(3, "Secondary School/\"O\" Level/SPM");
        a.put(4, "Higher Secondary/Pre-U/\"A\" Level/STPM");
        a.put(5, "Professional Certificate/Qualification");
        a.put(6, "Diploma");
        a.put(7, "Advanced/Higher Diploma");
        a.put(8, "Bachelor's Degree");
        a.put(9, "Post Graduate Diploma");
        a.put(10, "Professional Degree");
        a.put(11, "Master's Degree");
        a.put(12, "Doctorate Degree");
        return a;
    }

    /*
    "educationField": {
        "1": "Advertising/Media",
        "2": "Agriculture",
        "3": "Airline Transport",
        "4": "Architecture/Urban Studies",
        "5": "Art & Design",
        "6": "Biology",
        "7": "Business Studies/Administration/Management",
        "8": "Chemistry",
        "9": "Computer Science",
        "10": "Dentistry",
        "11": "Economics",
        "12": "Editing & Publication",
        "13": "Education/Teaching/Training",
        "14": "Engineering - Aviation/Aeronautics/Astronautics",
        "15": "Engineering - Chemical",
        "16": "Engineering - Civil",
        "17": "Engineering - Computer/Telecommunication",
        "18": "Engineering - Electrical/Electronic",
        "19": "Engineering - Environmental/Health/Safety",
        "20": "Engineering - Industrial",
        "21": "Engineering - Marine",
        "22": "Engineering - Material Science",
        "23": "Engineering - Mechanical",
        "24": "Engineering - Metal Fabrication/Tool & Die/Welding",
        "25": "Engineering - Others",
        "26": "Engineering - Petroleum/Oil/Gas",
        "27": "Finance/Accountancy/Banking",
        "28": "Food & Beverage Preparation/Services",
        "29": "Management",
        "30": "Geografical Science",
        "31": "Hospitality/Tourism Management",
        "32": "Human Resource Management",
        "33": "Humanities/Liberal Arts",
        "34": "Information System",
        "35": "Information Technology",
        "36": "Land Transport",
        "37": "Law",
        "38": "Linguistics/Translation & Interpretation",
        "39": "Mass Communications",
        "40": "Mathematics",
        "41": "Medical Science",
        "42": "Medicine",
        "43": "Merchart Marine",
        "44": "Music/Performing Arts Studies",
        "45": "Nursing",
        "46": "Others",
        "47": "Personal Services & Building/Grounds",
        "48": "Services",
        "49": "Pharmacy/Pharmacology",
        "50": "Physics",
        "51": "Protective Services & Management",
        "52": "Sales & Marketing",
        "53": "Science & Technology",
        "54": "Secretarial",
        "55": "Textile/Fashion Design and Production",
        "56": "Veterinary"
    }
    */
    public static HashMap getEducationField(){
        HashMap<Integer, String> a = new HashMap<>();
        a.put(1, "Advertising/Media");
        a.put(2, "Agriculture");
        a.put(3, "Airline Transport");
        a.put(4, "Architecture/Urban Studies");
        a.put(5, "Art & Design");
        a.put(6, "Biology");
        a.put(7, "Business Studies/Administration/Management");
        a.put(8, "Chemistry");
        a.put(9, "Computer Science");
        a.put(10, "Dentistry");
        a.put(11, "Economics");
        a.put(12, "Editing & Publication");
        a.put(13, "Education/Teaching/Training");
        a.put(14, "Engineering - Aviation/Aeronautics/Astronautics");
        a.put(15, "Engineering - Chemical");
        a.put(16, "Engineering - Civil");
        a.put(17, "Engineering - Computer/Telecommunication");
        a.put(18, "Engineering - Electrical/Electronic");
        a.put(19, "Engineering - Environmental/Health/Safety");
        a.put(20, "Engineering - Industrial");
        a.put(21, "Engineering - Marine");
        a.put(22, "Engineering - Material Science");
        a.put(23, "Engineering - Mechanical");
        a.put(24, "Engineering - Metal Fabrication/Tool & Die/Welding");
        a.put(25, "Engineering - Others");
        a.put(26, "Engineering - Petroleum/Oil/Gas");
        a.put(27, "Finance/Accountancy/Banking");
        a.put(28, "Food & Beverage Preparation/Services");
        a.put(29, "Management");
        a.put(30, "Geografical Science");
        a.put(31, "Hospitality/Tourism Management");
        a.put(32, "Human Resource Management");
        a.put(33, "Humanities/Liberal Arts");
        a.put(34, "Information System");
        a.put(35, "Information Technology");
        a.put(36, "Land Transport");
        a.put(37, "Law");
        a.put(38, "Linguistics/Translation & Interpretation");
        a.put(39, "Mass Communications");
        a.put(40, "Mathematics");
        a.put(41, "Medical Science");
        a.put(42, "Medicine");
        a.put(43, "Merchart Marine");
        a.put(44, "Music/Performing Arts Studies");
        a.put(45, "Nursing");
        a.put(46, "Others");
        a.put(47, "Personal Services & Building/Grounds");
        a.put(48, "Services");
        a.put(49, "Pharmacy/Pharmacology");
        a.put(50, "Physics");
        a.put(51, "Protective Services & Management");
        a.put(52, "Sales & Marketing");
        a.put(53, "Science & Technology");
        a.put(54, "Secretarial");
        a.put(55, "Textile/Fashion Design and Production");
        a.put(56, "Veterinary");
        return a;
    }

    public static HashMap getState(){
        HashMap<Integer, String> a = new HashMap<>();
        a.put(4, "Johor");
        a.put(10,"Kedah");
        a.put(22,"Kelantan");
        a.put(28,"Melaka");
        a.put(34,"Negeri Sembilan");
        a.put(39,"Penang");
        a.put(44,"Pahang");
        a.put(48,"Perak");
        a.put(52,"Perlis");
        a.put(56,"Sabah");
        a.put(60,"Selangor");
        a.put(64,"Sarawak");
        a.put(68,"Terengganu");
        a.put(103,"Kuala Lumpur");
        a.put(104,"Labuan");
        a.put(365,"Putrajaya");
        return a;
    }

    public static HashMap getCountry(){
        HashMap<Integer, String> a = new HashMap<>();
        a.put(1,"Afghanistan");
        a.put(2,"Albania");
        a.put(3,"Algeria");
        a.put(4,"American Samoa");
        a.put(5,"Andorra");
        a.put(6,"Angola");
        a.put(7,"Anguilla");
        a.put(8,"Antarctica");
        a.put(9,"Antigua and Barbuda");
        a.put(10,"Argentina");
        a.put(11,"Armenia");
        a.put(12,"Aruba");
        a.put(13,"Australia");
        a.put(14,"Austria");
        a.put(15,"Azerbaijan");
        a.put(16,"Bahamas");
        a.put(17,"Bahrain");
        a.put(18,"Bangladesh");
        a.put(19,"Barbados");
        a.put(20,"Belarus");
        a.put(21,"Belgium");
        a.put(22,"Belize");
        a.put(23,"Benin");
        a.put(24,"Bermuda");
        a.put(25,"Bhutan");
        a.put(26,"Bolivia");
        a.put(27,"Bosnia Hercegovina");
        a.put(28,"Botswana");
        a.put(29,"Bouvet Island");
        a.put(30,"Brazil");
        a.put(31,"British Indian Ocean Territory");
        a.put(32,"Brunei Darussalam");
        a.put(33,"Bulgaria");
        a.put(34,"Burkina Faso");
        a.put(35,"Burundi");
        a.put(36,"Cambodia");
        a.put(37,"Cameroon");
        a.put(38,"Canada");
        a.put(39,"Cape Verde");
        a.put(40,"Cayman Islands");
        a.put(41,"Central African Republic");
        a.put(42,"Chad");
        a.put(43,"Chile");
        a.put(44,"China");
        a.put(45,"Christmas Island");
        a.put(46,"Cocos (Keeling) Islands");
        a.put(47,"Colombia");
        a.put(48,"Comoros");
        a.put(49,"Congo");
        a.put(50,"Cook Islands");
        a.put(51,"Costa Rica");
        a.put(52,"Cote D'ivoire");
        a.put(53,"Croatia");
        a.put(54,"Cuba");
        a.put(55,"Cyprus");
        a.put(56,"Czech Republic");
        a.put(57,"Czechoslovakia");
        a.put(58,"Denmark");
        a.put(59,"Djibouti");
        a.put(60,"Dominica");
        a.put(61,"Dominican Republic");
        a.put(62,"East Timor");
        a.put(63,"Ecuador");
        a.put(64,"Egypt");
        a.put(65,"El Salvador");
        a.put(66,"Equatorial Guinea");
        a.put(67,"Estonia");
        a.put(68,"Ethiopia");
        a.put(69,"Falkland Islands (Malvinas)");
        a.put(70,"Faroe Islands");
        a.put(71,"Fiji");
        a.put(72,"Finland");
        a.put(73,"France");
        a.put(74,"French Guiana");
        a.put(75,"French Polynesia");
        a.put(76,"French Southern Territories");
        a.put(77,"Gabon");
        a.put(78,"Gambia");
        a.put(79,"Georgia");
        a.put(80,"Germany");
        a.put(81,"Ghana");
        a.put(82,"Gibraltar");
        a.put(83,"Greece");
        a.put(84,"Greenland");
        a.put(85,"Grenada");
        a.put(86,"Guadeloupe");
        a.put(87,"Guam");
        a.put(88,"Guatemala");
        a.put(89,"Guinea");
        a.put(90,"Guinea-Bissau");
        a.put(91,"Guyana");
        a.put(92,"Haiti");
        a.put(93,"Heard and Mc Donald Islands");
        a.put(94,"Honduras");
        a.put(95,"Hong Kong");
        a.put(96,"Hungary");
        a.put(97,"Iceland");
        a.put(98,"India");
        a.put(99,"Indonesia");
        a.put(100, "Iran");
        a.put(101,"Iraq");
        a.put(102,"Ireland");
        a.put(103,"Israel");
        a.put(104,"Italy");
        a.put(105,"Jamaica");
        a.put(106,"Japan");
        a.put(107,"Jordan");
        a.put(108,"Kazakhstan");
        a.put(109,"Kenya");
        a.put(110,"Kiribati");
        a.put(111,"Korea (North)");
        a.put(112,"Korea (South)");
        a.put(113,"Kuwait");
        a.put(114,"Kyrgyzstan");
        a.put(115,"Laos");
        a.put(116,"Latvia");
        a.put(117,"Lebanon");
        a.put(118,"Lesotho");
        a.put(119,"Liberia");
        a.put(120,"Libyan Arab Jamahiriya");
        a.put(121,"Liechtenstein");
        a.put(122,"Lithuania");
        a.put(123,"Luxembourg");
        a.put(124,"Macau");
        a.put(125,"Madagascar");
        a.put(126,"Malawi");
        a.put(127,"Malaysia");
        a.put(128,"Maldives");
        a.put(129,"Mali");
        a.put(130,"Malta");
        a.put(131,"Marshall Islands");
        a.put(132,"Martinique");
        a.put(133,"Mauritania");
        a.put(134,"Mauritius");
        a.put(135,"Mexico");
        a.put(136,"Micronesia");
        a.put(137,"Monaco");
        a.put(138,"Mongolia");
        a.put(139,"Montserrat");
        a.put(140,"Morocco");
        a.put(141,"Mozambique");
        a.put(142,"Myanmar");
        a.put(143,"Namibia");
        a.put(144,"Nauru");
        a.put(145,"Nepal");
        a.put(146,"Netherlands");
        a.put(147,"Netherlands Antilles");
        a.put(149,"New Caledonia");
        a.put(150,"New Zealand");
        a.put(151,"Nicaragua");
        a.put(152,"Niger");
        a.put(153,"Nigeria");
        a.put(154,"Niue");
        a.put(155,"Norfolk Island");
        a.put(156,"Northern Mariana Islands");
        a.put(157,"Norway");
        a.put(158,"Oman");
        a.put(160,"Pakistan");
        a.put(161,"Palau");
        a.put(162,"Panama");
        a.put(163,"Papua New Guinea");
        a.put(164,"Paraguay");
        a.put(165,"Peru");
        a.put(166,"Philippines");
        a.put(167,"Pitcairn");
        a.put(168,"Poland");
        a.put(169,"Portugal");
        a.put(170,"Puerto Rico");
        a.put(171,"Qatar");
        a.put(172,"Republic Of Moldova");
        a.put(173,"Reunion");
        a.put(174,"Romania");
        a.put(175,"Russia");
        a.put(176,"Rwanda");
        a.put(177,"Saint Kitts And Nevis");
        a.put(178,"Saint Lucia");
        a.put(179,"Saint Vincent and The Grenadines");
        a.put(180,"Samoa");
        a.put(181,"San Marino");
        a.put(182,"Sao Tome and Principe");
        a.put(183,"Saudi Arabia");
        a.put(184,"Senegal");
        a.put(185,"Seychelles");
        a.put(186,"Sierra Leone");
        a.put(187,"Singapore");
        a.put(188,"Slovakia");
        a.put(189,"Slovenia");
        a.put(190,"Solomon Islands");
        a.put(191,"Somalia");
        a.put(192,"South Africa");
        a.put(193,"Spain");
        a.put(194,"Sri Lanka");
        a.put(195,"St. Helena");
        a.put(196,"St. Pierre and Miquelon");
        a.put(197,"Sudan");
        a.put(198,"Suriname");
        a.put(199,"Svalbard and Jan Mayen Islands");
        a.put(200,"Swaziland");
        a.put(201,"Sweden");
        a.put(202,"Switzerland");
        a.put(203,"Syrian Arab Republic");
        a.put(204,"Taiwan");
        a.put(205,"Tajikistan");
        a.put(206,"Tanzania");
        a.put(207,"Thailand");
        a.put(208,"Togo");
        a.put(209,"Tokelau");
        a.put(210,"Tonga");
        a.put(211,"Trinidad and Tobago");
        a.put(212,"Tunisia");
        a.put(213,"Turkey");
        a.put(214,"Turkmenistan");
        a.put(215,"Turks and Caicos Islands");
        a.put(216,"Tuvalu");
        a.put(217,"Uganda");
        a.put(218,"Ukraine");
        a.put(219,"United Arab Emirates");
        a.put(220,"United Kingdom");
        a.put(221,"United States");
        a.put(222,"United States Minor Outlying Islands");
        a.put(223,"Uruguay");
        a.put(225,"Uzbekistan");
        a.put(226,"Vanuatu");
        a.put(227,"Vatican City State (Holy See)");
        a.put(228,"Venezuela");
        a.put(229,"Vietnam");
        a.put(230,"Virgin Islands (British)");
        a.put(231,"Virgin Islands (U.S.)");
        a.put(232,"Wallis and Futuna Islands");
        a.put(233,"Western Sahara");
        a.put(234,"Yemen");
        a.put(235,"Yugoslavia");
        a.put(236,"Zaire");
        a.put(237,"Zambia");
        a.put(238,"Zimbabwe");
        a.put(239,"Macedonia");
        return a;
    }

    public static HashMap getDialCode(){
        HashMap<String, String> a = new HashMap<>();
        a.put("(+60)","Malaysia");
        a.put("(+44)","UK");
        return a;
    }

    public static HashMap getApplicationStatus(){
        HashMap<Integer, String> a = new HashMap<>();
        a.put(0, "Unprocessed");
        a.put(1, "Shortlisted");
        a.put(2, "Interview");
        a.put(4, "Rejected");
        a.put(6, "KIV");
        a.put(9, "Pre-Screened");
        a.put(10, "Withdraw");
        a.put(11, "Hired");
        return a;
    }

    public static String date( String currentDate, String outputFormat, String inputFormat ){
        String newDate = "";
        if( outputFormat == null ){
            outputFormat = "dd MMM yyyy";
        }

        if( inputFormat == null ){
            inputFormat = "yyyy-MM-dd hh:mm:ss";
        }

        SimpleDateFormat outputFormatter = new SimpleDateFormat(outputFormat, Locale.getDefault());
        try {
            Date theDate;
            if( currentDate == null ){
                Calendar cal = Calendar.getInstance();
                theDate = cal.getTime();
            }else{
                SimpleDateFormat inputFormatter = new SimpleDateFormat(inputFormat, Locale.getDefault());
                theDate = inputFormatter.parse(currentDate);
            }
            newDate = outputFormatter.format(theDate);
        } catch (java.text.ParseException e) {
            Log.e("newdateErr", e.getMessage());
        }
        return newDate;
    }

    /*
    * month-year duration only
    * supported date format == yyyy-MM-dd
    * */
    public static String calculateDuration(String startDate, String endDate){
        SimpleDateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            Date _startDate = outputFormatter.parse(startDate);
            Date _endDate = outputFormatter.parse(endDate);

            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(_startDate);
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(_endDate);

            //int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);

            int diffMonth = 0;
            int diffYear = 0;

            int yearStart = startCalendar.get(Calendar.YEAR);
            int yearEnd = endCalendar.get(Calendar.YEAR);
            int monthStart = startCalendar.get(Calendar.MONTH);
            int monthEnd = endCalendar.get(Calendar.MONTH);

            if( yearStart == yearEnd ){ // yearStart == yearEnd
                diffYear = 0;
                diffMonth = monthEnd - monthStart;
            }else{ // yearStart < yearEnd
                diffYear = yearEnd - yearStart;
                if( monthEnd < monthStart ){
                    diffYear--;
                    diffMonth = (monthEnd+12) - monthStart;
                }else{
                    diffMonth = monthEnd - monthStart;
                }
            }

            return diffYear+"y "+diffMonth+"m";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String[] listOfMonth(){
        ArrayList<String> _listOfMonth = new ArrayList<>();

        _listOfMonth.add("--none--");
        for( int i=1; i <= 12; i++ ){
            String dateStr = "01 "+( i < 10 ? "0"+i : i )+" 2016";
            _listOfMonth.add(Jenjobs.date(dateStr, "MMMM", "dd MM yyyy"));
        }
        return _listOfMonth.toArray(new String[_listOfMonth.size()]);
    }

    public static String[] listOfYear(){
        ArrayList<String> _listOfYear = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        _listOfYear.add("--none--");
        int year = cal.get(Calendar.YEAR);
        for( int i=year-20; i <= year; i++ ){
            _listOfYear.add(String.valueOf(i));
        }

        return _listOfYear.toArray(new String[_listOfYear.size()]);
    }


    public void saveFile(File file){

    }
}
