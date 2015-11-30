package com.example.william.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CountryAdapter extends BaseAdapter implements ListAdapter {

    public ArrayList<Country> country = new ArrayList<>();
    private Context context;

    public CountryAdapter(Context context){
        this.context = context;

        country.add(new Country(1,"Afghanistan"));
        country.add(new Country(2,"Albania"));
        country.add(new Country(3,"Algeria"));
        country.add(new Country(4,"American Samoa"));
        country.add(new Country(5,"Andorra"));
        country.add(new Country(6,"Angola"));
        country.add(new Country(7,"Anguilla"));
        country.add(new Country(8,"Antarctica"));
        country.add(new Country(9,"Antigua and Barbuda"));
        country.add(new Country(10,"Argentina"));
        country.add(new Country(11,"Armenia"));
        country.add(new Country(12,"Aruba"));
        country.add(new Country(13,"Australia"));
        country.add(new Country(14,"Austria"));
        country.add(new Country(15,"Azerbaijan"));
        country.add(new Country(16,"Bahamas"));
        country.add(new Country(17,"Bahrain"));
        country.add(new Country(18,"Bangladesh"));
        country.add(new Country(19,"Barbados"));
        country.add(new Country(20,"Belarus"));
        country.add(new Country(21,"Belgium"));
        country.add(new Country(22,"Belize"));
        country.add(new Country(23,"Benin"));
        country.add(new Country(24,"Bermuda"));
        country.add(new Country(25,"Bhutan"));
        country.add(new Country(26,"Bolivia"));
        country.add(new Country(27,"Bosnia Hercegovina"));
        country.add(new Country(28,"Botswana"));
        country.add(new Country(29,"Bouvet Island"));
        country.add(new Country(30,"Brazil"));
        country.add(new Country(31,"British Indian Ocean Territory"));
        country.add(new Country(32,"Brunei Darussalam"));
        country.add(new Country(33,"Bulgaria"));
        country.add(new Country(34,"Burkina Faso"));
        country.add(new Country(35,"Burundi"));
        country.add(new Country(36,"Cambodia"));
        country.add(new Country(37,"Cameroon"));
        country.add(new Country(38,"Canada"));
        country.add(new Country(39,"Cape Verde"));
        country.add(new Country(40,"Cayman Islands"));
        country.add(new Country(41,"Central African Republic"));
        country.add(new Country(42,"Chad"));
        country.add(new Country(43,"Chile"));
        country.add(new Country(44,"China"));
        country.add(new Country(45,"Christmas Island"));
        country.add(new Country(46,"Cocos (Keeling) Islands"));
        country.add(new Country(47,"Colombia"));
        country.add(new Country(48,"Comoros"));
        country.add(new Country(49,"Congo"));
        country.add(new Country(50,"Cook Islands"));
        country.add(new Country(51,"Costa Rica"));
        country.add(new Country(52,"Cote D'ivoire"));
        country.add(new Country(53,"Croatia"));
        country.add(new Country(54,"Cuba"));
        country.add(new Country(55,"Cyprus"));
        country.add(new Country(56,"Czech Republic"));
        country.add(new Country(57,"Czechoslovakia"));
        country.add(new Country(58,"Denmark"));
        country.add(new Country(59,"Djibouti"));
        country.add(new Country(60,"Dominica"));
        country.add(new Country(61,"Dominican Republic"));
        country.add(new Country(62,"East Timor"));
        country.add(new Country(63,"Ecuador"));
        country.add(new Country(64,"Egypt"));
        country.add(new Country(65,"El Salvador"));
        country.add(new Country(66,"Equatorial Guinea"));
        country.add(new Country(67,"Estonia"));
        country.add(new Country(68,"Ethiopia"));
        country.add(new Country(69,"Falkland Islands (Malvinas)"));
        country.add(new Country(70,"Faroe Islands"));
        country.add(new Country(71,"Fiji"));
        country.add(new Country(72,"Finland"));
        country.add(new Country(73,"France"));
        country.add(new Country(74,"French Guiana"));
        country.add(new Country(75,"French Polynesia"));
        country.add(new Country(76,"French Southern Territories"));
        country.add(new Country(77,"Gabon"));
        country.add(new Country(78,"Gambia"));
        country.add(new Country(79,"Georgia"));
        country.add(new Country(80,"Germany"));
        country.add(new Country(81,"Ghana"));
        country.add(new Country(82,"Gibraltar"));
        country.add(new Country(83,"Greece"));
        country.add(new Country(84,"Greenland"));
        country.add(new Country(85,"Grenada"));
        country.add(new Country(86,"Guadeloupe"));
        country.add(new Country(87,"Guam"));
        country.add(new Country(88,"Guatemala"));
        country.add(new Country(89,"Guinea"));
        country.add(new Country(90,"Guinea-Bissau"));
        country.add(new Country(91,"Guyana"));
        country.add(new Country(92,"Haiti"));
        country.add(new Country(93,"Heard and Mc Donald Islands"));
        country.add(new Country(94,"Honduras"));
        country.add(new Country(95,"Hong Kong"));
        country.add(new Country(96,"Hungary"));
        country.add(new Country(97,"Iceland"));
        country.add(new Country(98,"India"));
        country.add(new Country(99,"Indonesia"));
        country.add(new Country(100, "Iran"));
        country.add(new Country(101,"Iraq"));
        country.add(new Country(102,"Ireland"));
        country.add(new Country(103,"Israel"));
        country.add(new Country(104,"Italy"));
        country.add(new Country(105,"Jamaica"));
        country.add(new Country(106,"Japan"));
        country.add(new Country(107,"Jordan"));
        country.add(new Country(108,"Kazakhstan"));
        country.add(new Country(109,"Kenya"));
        country.add(new Country(110,"Kiribati"));
        country.add(new Country(111,"Korea (North)"));
        country.add(new Country(112,"Korea (South)"));
        country.add(new Country(113,"Kuwait"));
        country.add(new Country(114,"Kyrgyzstan"));
        country.add(new Country(115,"Laos"));
        country.add(new Country(116,"Latvia"));
        country.add(new Country(117,"Lebanon"));
        country.add(new Country(118,"Lesotho"));
        country.add(new Country(119,"Liberia"));
        country.add(new Country(120,"Libyan Arab Jamahiriya"));
        country.add(new Country(121,"Liechtenstein"));
        country.add(new Country(122,"Lithuania"));
        country.add(new Country(123,"Luxembourg"));
        country.add(new Country(124,"Macau"));
        country.add(new Country(125,"Madagascar"));
        country.add(new Country(126,"Malawi"));
        country.add(new Country(127,"Malaysia"));
        country.add(new Country(128,"Maldives"));
        country.add(new Country(129,"Mali"));
        country.add(new Country(130,"Malta"));
        country.add(new Country(131,"Marshall Islands"));
        country.add(new Country(132,"Martinique"));
        country.add(new Country(133,"Mauritania"));
        country.add(new Country(134,"Mauritius"));
        country.add(new Country(135,"Mexico"));
        country.add(new Country(136,"Micronesia"));
        country.add(new Country(137,"Monaco"));
        country.add(new Country(138,"Mongolia"));
        country.add(new Country(139,"Montserrat"));
        country.add(new Country(140,"Morocco"));
        country.add(new Country(141,"Mozambique"));
        country.add(new Country(142,"Myanmar"));
        country.add(new Country(143,"Namibia"));
        country.add(new Country(144,"Nauru"));
        country.add(new Country(145,"Nepal"));
        country.add(new Country(146,"Netherlands"));
        country.add(new Country(147,"Netherlands Antilles"));
        country.add(new Country(149,"New Caledonia"));
        country.add(new Country(150,"New Zealand"));
        country.add(new Country(151,"Nicaragua"));
        country.add(new Country(152,"Niger"));
        country.add(new Country(153,"Nigeria"));
        country.add(new Country(154,"Niue"));
        country.add(new Country(155,"Norfolk Island"));
        country.add(new Country(156,"Northern Mariana Islands"));
        country.add(new Country(157,"Norway"));
        country.add(new Country(158,"Oman"));
        country.add(new Country(160,"Pakistan"));
        country.add(new Country(161,"Palau"));
        country.add(new Country(162,"Panama"));
        country.add(new Country(163,"Papua New Guinea"));
        country.add(new Country(164,"Paraguay"));
        country.add(new Country(165,"Peru"));
        country.add(new Country(166,"Philippines"));
        country.add(new Country(167,"Pitcairn"));
        country.add(new Country(168,"Poland"));
        country.add(new Country(169,"Portugal"));
        country.add(new Country(170,"Puerto Rico"));
        country.add(new Country(171,"Qatar"));
        country.add(new Country(172,"Republic Of Moldova"));
        country.add(new Country(173,"Reunion"));
        country.add(new Country(174,"Romania"));
        country.add(new Country(175,"Russia"));
        country.add(new Country(176,"Rwanda"));
        country.add(new Country(177,"Saint Kitts And Nevis"));
        country.add(new Country(178,"Saint Lucia"));
        country.add(new Country(179,"Saint Vincent and The Grenadines"));
        country.add(new Country(180,"Samoa"));
        country.add(new Country(181,"San Marino"));
        country.add(new Country(182,"Sao Tome and Principe"));
        country.add(new Country(183,"Saudi Arabia"));
        country.add(new Country(184,"Senegal"));
        country.add(new Country(185,"Seychelles"));
        country.add(new Country(186,"Sierra Leone"));
        country.add(new Country(187,"Singapore"));
        country.add(new Country(188,"Slovakia"));
        country.add(new Country(189,"Slovenia"));
        country.add(new Country(190,"Solomon Islands"));
        country.add(new Country(191,"Somalia"));
        country.add(new Country(192,"South Africa"));
        country.add(new Country(193,"Spain"));
        country.add(new Country(194,"Sri Lanka"));
        country.add(new Country(195,"St. Helena"));
        country.add(new Country(196,"St. Pierre and Miquelon"));
        country.add(new Country(197,"Sudan"));
        country.add(new Country(198,"Suriname"));
        country.add(new Country(199,"Svalbard and Jan Mayen Islands"));
        country.add(new Country(200,"Swaziland"));
        country.add(new Country(201,"Sweden"));
        country.add(new Country(202,"Switzerland"));
        country.add(new Country(203,"Syrian Arab Republic"));
        country.add(new Country(204,"Taiwan"));
        country.add(new Country(205,"Tajikistan"));
        country.add(new Country(206,"Tanzania"));
        country.add(new Country(207,"Thailand"));
        country.add(new Country(208,"Togo"));
        country.add(new Country(209,"Tokelau"));
        country.add(new Country(210,"Tonga"));
        country.add(new Country(211,"Trinidad and Tobago"));
        country.add(new Country(212,"Tunisia"));
        country.add(new Country(213,"Turkey"));
        country.add(new Country(214,"Turkmenistan"));
        country.add(new Country(215,"Turks and Caicos Islands"));
        country.add(new Country(216,"Tuvalu"));
        country.add(new Country(217,"Uganda"));
        country.add(new Country(218,"Ukraine"));
        country.add(new Country(219,"United Arab Emirates"));
        country.add(new Country(220,"United Kingdom"));
        country.add(new Country(221,"United States"));
        country.add(new Country(222,"United States Minor Outlying Islands"));
        country.add(new Country(223,"Uruguay"));
        country.add(new Country(225,"Uzbekistan"));
        country.add(new Country(226,"Vanuatu"));
        country.add(new Country(227,"Vatican City State (Holy See)"));
        country.add(new Country(228,"Venezuela"));
        country.add(new Country(229,"Vietnam"));
        country.add(new Country(230,"Virgin Islands (British)"));
        country.add(new Country(231,"Virgin Islands (U.S.)"));
        country.add(new Country(232,"Wallis and Futuna Islands"));
        country.add(new Country(233,"Western Sahara"));
        country.add(new Country(234,"Yemen"));
        country.add(new Country(235,"Yugoslavia"));
        country.add(new Country(236,"Zaire"));
        country.add(new Country(237,"Zambia"));
        country.add(new Country(238,"Zimbabwe"));
        country.add(new Country(239,"Macedonia"));
    }

    @Override
    public int getCount() {
        return country.size();
    }

    @Override
    public Object getItem(int position) {
        return country.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            v = vi.inflate(R.layout.spinner_item, parent, false);
        }

        Country c = (Country) getItem(position);

        TextView tvName = (TextView) v.findViewById(R.id.spinner_item);
        tvName.setText(c.name);

        return v;
    }
}
