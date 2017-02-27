package com.drdc1.medic;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 *
 */

public class NameSuggestion implements SearchSuggestion {
    private String soildierName;

    public static final Creator<NameSuggestion> CREATOR = new Creator<NameSuggestion>() {
        @Override
        public NameSuggestion createFromParcel(Parcel in) {
            return new NameSuggestion(in);
        }

        @Override
        public NameSuggestion[] newArray(int size) {
            return new NameSuggestion[size];
        }
    };

    public NameSuggestion(Parcel source) {
        this.soildierName = source.readString();
    }

    public NameSuggestion(String suggestion) {
        this.soildierName = suggestion;
    }

    @Override
    public String getBody() {

        return soildierName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(soildierName);

    }
}
