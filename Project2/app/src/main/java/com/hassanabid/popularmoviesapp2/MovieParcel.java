package com.hassanabid.popularmoviesapp2;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hassanabid on 8/24/15.
 */
public class MovieParcel implements Parcelable{
    public int id;
    public String title;
    public String poster;
    public String overview;
    public String release_date;
    public String vote;

    public MovieParcel(int id,String title, String poster, String overview, String release_date, String vote)
    {
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.overview = overview;
        this.release_date = release_date;
        this.vote = vote;
    }

    private MovieParcel(Parcel in){
        title = in.readString();
        poster = in.readString();
        overview = in.readString();
        release_date = in.readString();
        vote = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() { return title + "--" + poster + "--" + overview; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(poster);
        parcel.writeString(overview);
        parcel.writeString(release_date);
        parcel.writeString(vote);
    }

    public final Creator<MovieParcel> CREATOR = new Creator<MovieParcel>() {
        @Override
        public MovieParcel createFromParcel(Parcel parcel) {
            return new MovieParcel(parcel);
        }

        @Override
        public MovieParcel[] newArray(int i) {
            return new MovieParcel[i];
        }

    };
}
