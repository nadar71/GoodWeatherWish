package eu.indiewalk.mystic.weatherapp.data.database;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

@Database(entities = {WeatherEntry.class}, version = 1)
@TypeConverters(DateConverter.class)
public abstract class WeatherAppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "weather";

    // for singleton init
    private static final    Object LOCK = new Object();
    private static volatile WeatherAppDatabase singleInstance;


    // getters for the dao
    public abstract WeatherDao weatherDao();


    // must have only a database instance : singleton creation
    public static WeatherAppDatabase getInstance(Context context){
        if (singleInstance == null){
            synchronized (LOCK){
                if (singleInstance == null){
                    singleInstance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            WeatherAppDatabase.class,
                            WeatherAppDatabase.DATABASE_NAME).build();
                }
            }
        }
        return singleInstance;
    }


}
