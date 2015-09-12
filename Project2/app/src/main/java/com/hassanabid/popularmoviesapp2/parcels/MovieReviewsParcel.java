package com.hassanabid.popularmoviesapp2.parcels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hassanabid on 9/12/15.
 */
public class MovieReviewsParcel  implements Parcelable {

    public String author;
    public String content;
    public MovieReviewsParcel(String author,String content)
    {
        this.author = author;
        this.content = content;
    }

    private MovieReviewsParcel(Parcel in){
        author = in.readString();
        content = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() { return content; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(author);
        parcel.writeString(content);
    }

    public final Creator<MovieReviewsParcel> CREATOR = new Creator<MovieReviewsParcel>() {
        @Override
        public MovieReviewsParcel createFromParcel(Parcel parcel) {
            return new MovieReviewsParcel(parcel);
        }

        @Override
        public MovieReviewsParcel[] newArray(int i) {
            return new MovieReviewsParcel[i];
        }

    };
}

