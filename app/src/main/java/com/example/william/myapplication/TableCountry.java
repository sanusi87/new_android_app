package com.example.william.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class TableCountry extends SQLiteOpenHelper{
    public static final String TABLE_NAME = "country";
    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TableCountry.TABLE_NAME+"' (id INTEGER(4), name TEXT, dial_code TEXT);";
    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TableCountry.TABLE_NAME+"'";

    public SQLiteDatabase db;
    public TableCountry(Context context){
        super(context, Jenjobs.DATABASE_NAME , null, Jenjobs.DATABASE_VERSION);
        db = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL(SQL_DELETE_ENTRIES);
        //onCreate(db);
    }

    public static ArrayList<String[]> initialise(){
        ArrayList<String[]> countries = new ArrayList<>();
        countries.add(new String[]{"1","Afghanistan","93"});
        countries.add(new String[]{"2","Albania","355"});
        countries.add(new String[]{"3","Algeria","213"});
        countries.add(new String[]{"4","American Samoa","1684"});
        countries.add(new String[]{"5","Andorra","376"});
        countries.add(new String[]{"6","Angola","244"});
        countries.add(new String[]{"7","Anguilla","1264"});
        countries.add(new String[]{"8","Antarctica","0"});
        countries.add(new String[]{"9","Antigua and Barbuda","1268"});
        countries.add(new String[]{"10","Argentina","54"});
        countries.add(new String[]{"11","Armenia","374"});
        countries.add(new String[]{"12","Aruba","297"});
        countries.add(new String[]{"13","Australia","61"});
        countries.add(new String[]{"14","Austria","43"});
        countries.add(new String[]{"15","Azerbaijan","994"});
        countries.add(new String[]{"16","Bahamas","1242"});
        countries.add(new String[]{"17","Bahrain","973"});
        countries.add(new String[]{"18","Bangladesh","880"});
        countries.add(new String[]{"19","Barbados","1246"});
        countries.add(new String[]{"20","Belarus","375"});
        countries.add(new String[]{"21","Belgium","32"});
        countries.add(new String[]{"22","Belize","501"});
        countries.add(new String[]{"23","Benin","229"});
        countries.add(new String[]{"24","Bermuda","1441"});
        countries.add(new String[]{"25","Bhutan","975"});
        countries.add(new String[]{"26","Bolivia","591"});
        countries.add(new String[]{"27","Bosnia Hercegovina","387"});
        countries.add(new String[]{"28","Botswana","267"});
        countries.add(new String[]{"29","Bouvet Island","0"});
        countries.add(new String[]{"30","Brazil","55"});
        countries.add(new String[]{"31","British Indian Ocean Territory","246"});
        countries.add(new String[]{"32","Brunei Darussalam","673"});
        countries.add(new String[]{"33","Bulgaria","359"});
        countries.add(new String[]{"34","Burkina Faso","226"});
        countries.add(new String[]{"35","Burundi","257"});
        countries.add(new String[]{"36","Cambodia","855"});
        countries.add(new String[]{"37","Cameroon","237"});
        countries.add(new String[]{"38","Canada","1"});
        countries.add(new String[]{"39","Cape Verde","238"});
        countries.add(new String[]{"40","Cayman Islands","1345"});
        countries.add(new String[]{"41","Central African Republic","236"});
        countries.add(new String[]{"42","Chad","235"});
        countries.add(new String[]{"43","Chile","56"});
        countries.add(new String[]{"44","China","86"});
        countries.add(new String[]{"45","Christmas Island","61"});
        countries.add(new String[]{"46","Cocos (Keeling) Islands","61"});
        countries.add(new String[]{"47","Colombia","57"});
        countries.add(new String[]{"48","Comoros","269"});
        countries.add(new String[]{"49","Congo","242"});
        countries.add(new String[]{"50","Cook Islands","682"});
        countries.add(new String[]{"51","Costa Rica","506"});
        countries.add(new String[]{"52","Cote D'ivoire","225"});
        countries.add(new String[]{"53","Croatia","385"});
        countries.add(new String[]{"54","Cuba","53"});
        countries.add(new String[]{"55","Cyprus","357"});
        countries.add(new String[]{"56","Czech Republic","420"});
        countries.add(new String[]{"57","Czechoslovakia","42"});
        countries.add(new String[]{"58","Denmark","45"});
        countries.add(new String[]{"59","Djibouti","253"});
        countries.add(new String[]{"60","Dominica","1767"});
        countries.add(new String[]{"61","Dominican Republic","1809"});
        countries.add(new String[]{"62","East Timor","670"});
        countries.add(new String[]{"63","Ecuador","593"});
        countries.add(new String[]{"64","Egypt","20"});
        countries.add(new String[]{"65","El Salvador","503"});
        countries.add(new String[]{"66","Equatorial Guinea","240"});
        countries.add(new String[]{"67","Estonia","372"});
        countries.add(new String[]{"68","Ethiopia","251"});
        countries.add(new String[]{"69","Falkland Islands (Malvinas)","500"});
        countries.add(new String[]{"70","Faroe Islands","298"});
        countries.add(new String[]{"71","Fiji","679"});
        countries.add(new String[]{"72","Finland","358"});
        countries.add(new String[]{"73","France","33"});
        countries.add(new String[]{"74","French Guiana","594"});
        countries.add(new String[]{"75","French Polynesia","689"});
        countries.add(new String[]{"76","French Southern Territories","0"});
        countries.add(new String[]{"77","Gabon","241"});
        countries.add(new String[]{"78","Gambia","220"});
        countries.add(new String[]{"79","Georgia","995"});
        countries.add(new String[]{"80","Germany","49"});
        countries.add(new String[]{"81","Ghana","233"});
        countries.add(new String[]{"82","Gibraltar","350"});
        countries.add(new String[]{"83","Greece","30"});
        countries.add(new String[]{"84","Greenland","299"});
        countries.add(new String[]{"85","Grenada","1473"});
        countries.add(new String[]{"86","Guadeloupe","590"});
        countries.add(new String[]{"87","Guam","1671"});
        countries.add(new String[]{"88","Guatemala","502"});
        countries.add(new String[]{"89","Guinea","224"});
        countries.add(new String[]{"90","Guinea-Bissau","245"});
        countries.add(new String[]{"91","Guyana","592"});
        countries.add(new String[]{"92","Haiti","509"});
        countries.add(new String[]{"93","Heard and Mc Donald Islands","0"});
        countries.add(new String[]{"94","Honduras","504"});
        countries.add(new String[]{"95","Hong Kong","852"});
        countries.add(new String[]{"96","Hungary","36"});
        countries.add(new String[]{"97","Iceland","354"});
        countries.add(new String[]{"98","India","91"});
        countries.add(new String[]{"99","Indonesia","62"});
        countries.add(new String[]{"100", "Iran", "98"});
        countries.add(new String[]{"101","Iraq","964"});
        countries.add(new String[]{"102","Ireland","353"});
        countries.add(new String[]{"103","Israel","972"});
        countries.add(new String[]{"104","Italy","39"});
        countries.add(new String[]{"105","Jamaica","1876"});
        countries.add(new String[]{"106","Japan","81"});
        countries.add(new String[]{"107","Jordan","962"});
        countries.add(new String[]{"108","Kazakhstan","76"});
        countries.add(new String[]{"109","Kenya","254"});
        countries.add(new String[]{"110","Kiribati","686"});
        countries.add(new String[]{"111","Korea (North)","850"});
        countries.add(new String[]{"112","Korea (South)","82"});
        countries.add(new String[]{"113","Kuwait","965"});
        countries.add(new String[]{"114","Kyrgyzstan","996"});
        countries.add(new String[]{"115","Laos","856"});
        countries.add(new String[]{"116","Latvia","371"});
        countries.add(new String[]{"117","Lebanon","961"});
        countries.add(new String[]{"118","Lesotho","266"});
        countries.add(new String[]{"119","Liberia","231"});
        countries.add(new String[]{"120","Libyan Arab Jamahiriya","218"});
        countries.add(new String[]{"121","Liechtenstein","423"});
        countries.add(new String[]{"122","Lithuania","370"});
        countries.add(new String[]{"123","Luxembourg","352"});
        countries.add(new String[]{"124","Macau","853"});
        countries.add(new String[]{"125","Madagascar","261"});
        countries.add(new String[]{"126","Malawi","265"});
        countries.add(new String[]{"127","Malaysia","60"});
        countries.add(new String[]{"128","Maldives","960"});
        countries.add(new String[]{"129","Mali","223"});
        countries.add(new String[]{"130","Malta","356"});
        countries.add(new String[]{"131","Marshall Islands","692"});
        countries.add(new String[]{"132","Martinique","596"});
        countries.add(new String[]{"133","Mauritania","222"});
        countries.add(new String[]{"134","Mauritius","230"});
        countries.add(new String[]{"135","Mexico","52"});
        countries.add(new String[]{"136","Micronesia","691"});
        countries.add(new String[]{"137","Monaco","377"});
        countries.add(new String[]{"138","Mongolia","976"});
        countries.add(new String[]{"139","Montserrat","1664"});
        countries.add(new String[]{"140","Morocco","212"});
        countries.add(new String[]{"141","Mozambique","258"});
        countries.add(new String[]{"142","Myanmar","95"});
        countries.add(new String[]{"143","Namibia","264"});
        countries.add(new String[]{"144","Nauru","674"});
        countries.add(new String[]{"145","Nepal","977"});
        countries.add(new String[]{"146","Netherlands","31"});
        countries.add(new String[]{"147","Netherlands Antilles","599"});
        countries.add(new String[]{"149","New Caledonia","687"});
        countries.add(new String[]{"150","New Zealand","64"});
        countries.add(new String[]{"151","Nicaragua","505"});
        countries.add(new String[]{"152","Niger","227"});
        countries.add(new String[]{"153","Nigeria","234"});
        countries.add(new String[]{"154","Niue","683"});
        countries.add(new String[]{"155","Norfolk Island","672"});
        countries.add(new String[]{"156","Northern Mariana Islands","1670"});
        countries.add(new String[]{"157","Norway","47"});
        countries.add(new String[]{"158","Oman","968"});
        countries.add(new String[]{"160","Pakistan","92"});
        countries.add(new String[]{"161","Palau","680"});
        countries.add(new String[]{"162","Panama","507"});
        countries.add(new String[]{"163","Papua New Guinea","675"});
        countries.add(new String[]{"164","Paraguay","595"});
        countries.add(new String[]{"165","Peru","51"});
        countries.add(new String[]{"166","Philippines","63"});
        countries.add(new String[]{"167","Pitcairn","64"});
        countries.add(new String[]{"168","Poland","48"});
        countries.add(new String[]{"169","Portugal","351"});
        countries.add(new String[]{"170","Puerto Rico","1787"});
        countries.add(new String[]{"171","Qatar","974"});
        countries.add(new String[]{"172","Republic Of Moldova","373"});
        countries.add(new String[]{"173","Reunion","262"});
        countries.add(new String[]{"174","Romania","40"});
        countries.add(new String[]{"175","Russia","7"});
        countries.add(new String[]{"176","Rwanda","250"});
        countries.add(new String[]{"177","Saint Kitts And Nevis","1869"});
        countries.add(new String[]{"178","Saint Lucia","1758"});
        countries.add(new String[]{"179","Saint Vincent and The Grenadines","1784"});
        countries.add(new String[]{"180","Samoa","685"});
        countries.add(new String[]{"181","San Marino","378"});
        countries.add(new String[]{"182","Sao Tome and Principe","239"});
        countries.add(new String[]{"183","Saudi Arabia","966"});
        countries.add(new String[]{"184","Senegal","221"});
        countries.add(new String[]{"185","Seychelles","248"});
        countries.add(new String[]{"186","Sierra Leone","232"});
        countries.add(new String[]{"187","Singapore","65"});
        countries.add(new String[]{"188","Slovakia","421"});
        countries.add(new String[]{"189","Slovenia","286"});
        countries.add(new String[]{"190","Solomon Islands","677"});
        countries.add(new String[]{"191","Somalia","252"});
        countries.add(new String[]{"192","South Africa","27"});
        countries.add(new String[]{"193","Spain","31"});
        countries.add(new String[]{"194","Sri Lanka","94"});
        countries.add(new String[]{"195","St. Helena","0"});
        countries.add(new String[]{"196","St. Pierre and Miquelon","508"});
        countries.add(new String[]{"197","Sudan","249"});
        countries.add(new String[]{"198","Suriname","597"});
        countries.add(new String[]{"199","Svalbard and Jan Mayen Islands","0"});
        countries.add(new String[]{"200","Swaziland","268"});
        countries.add(new String[]{"201","Sweden","46"});
        countries.add(new String[]{"202","Switzerland","41"});
        countries.add(new String[]{"203","Syrian Arab Republic","963"});
        countries.add(new String[]{"204","Taiwan","886"});
        countries.add(new String[]{"205","Tajikistan","992"});
        countries.add(new String[]{"206","Tanzania","255"});
        countries.add(new String[]{"207","Thailand","66"});
        countries.add(new String[]{"208","Togo","228"});
        countries.add(new String[]{"209","Tokelau","690"});
        countries.add(new String[]{"210","Tonga","676"});
        countries.add(new String[]{"211","Trinidad and Tobago","1868"});
        countries.add(new String[]{"212","Tunisia","216"});
        countries.add(new String[]{"213","Turkey","90"});
        countries.add(new String[]{"214","Turkmenistan","993"});
        countries.add(new String[]{"215","Turks and Caicos Islands","1649"});
        countries.add(new String[]{"216","Tuvalu","688"});
        countries.add(new String[]{"217","Uganda","256"});
        countries.add(new String[]{"218","Ukraine","380"});
        countries.add(new String[]{"219","United Arab Emirates","971"});
        countries.add(new String[]{"220","United Kingdom","44"});
        countries.add(new String[]{"221","United States","1"});
        countries.add(new String[]{"222","United States Minor Outlying Islands","0"});
        countries.add(new String[]{"223","Uruguay","598"});
        countries.add(new String[]{"225","Uzbekistan","998"});
        countries.add(new String[]{"226","Vanuatu","678"});
        countries.add(new String[]{"227","Vatican City State (Holy See)","379"});
        countries.add(new String[]{"228","Venezuela","58"});
        countries.add(new String[]{"229","Vietnam","84"});
        countries.add(new String[]{"230","Virgin Islands (British)","1"});
        countries.add(new String[]{"231","Virgin Islands (U.S.)","1"});
        countries.add(new String[]{"232","Wallis and Futuna Islands","681"});
        countries.add(new String[]{"233","Western Sahara","212"});
        countries.add(new String[]{"234","Yemen","967"});
        countries.add(new String[]{"235","Yugoslavia","0"});
        countries.add(new String[]{"236","Zaire","243"});
        countries.add(new String[]{"237","Zambia","260"});
        countries.add(new String[]{"238","Zimbabwe","263"});
        countries.add(new String[]{"239","Macedonia ","389"});

        return countries;
    }

    public ArrayList<Country> getCountries(){
        ArrayList<Country> countries = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM "+TableCountry.TABLE_NAME, null);
        c.moveToFirst();
        while( !c.isAfterLast() ){
            int countryId = c.getInt(0);
            String countryName = c.getString(1);
            String countryDialCode = c.getString(2);
            countries.add(new Country(countryId, countryName, countryDialCode));
            c.moveToNext();
        }
        return countries;
    }

    public Country findCountryById(int country_id) {
        Cursor c = db.rawQuery("SELECT * FROM " + TableCountry.TABLE_NAME + " WHERE id=?", new String[]{String.valueOf(country_id)});
        c.moveToFirst();
        if( !c.isAfterLast() ){
            Country cc = new Country(c.getInt(0), c.getString(1), c.getString(2));
            return cc;
        }
        return null;
    }
}
