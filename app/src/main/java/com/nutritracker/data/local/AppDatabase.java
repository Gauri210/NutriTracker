package com.nutritracker.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.nutritracker.data.local.dao.DailyProgressDao;
import com.nutritracker.data.local.dao.FoodDao;
import com.nutritracker.data.local.dao.MealDao;
import com.nutritracker.data.local.dao.UserDao;
import com.nutritracker.data.local.entity.DailyProgress;
import com.nutritracker.data.local.entity.FoodItem;
import com.nutritracker.data.local.entity.MealEntry;
import com.nutritracker.data.local.entity.ServingUnit;
import com.nutritracker.data.local.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {UserEntity.class, FoodItem.class, ServingUnit.class, MealEntry.class, DailyProgress.class}, version = 8, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract FoodDao foodDao();
    public abstract MealDao mealDao();
    public abstract DailyProgressDao dailyProgressDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "nutritracker_v8.db")
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    android.util.Log.d("AppDatabase", "onCreate triggered");
                                }

                                @Override
                                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                    super.onOpen(db);
                                    android.util.Log.d("AppDatabase", "onOpen triggered");
                                    databaseWriteExecutor.execute(() -> {
                                        FoodDao dao = INSTANCE.foodDao();
                                        if (dao.getFoodCount() == 0) {
                                            android.util.Log.d("AppDatabase", "Database empty, seeding data...");
                                            seedDatabase(dao);
                                        } else {
                                            android.util.Log.d("AppDatabase", "Database already contains data: " + dao.getFoodCount() + " items");
                                        }
                                    });
                                }
                            })
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static void seedDatabase(FoodDao dao) {
        List<FoodItem> foods = new ArrayList<>();
        foods.add(new FoodItem("Dal Tadka", 110, 5.0, 15.0, 4.0, 3.5));
        foods.add(new FoodItem("Paneer Butter Masala", 220, 8.5, 6.0, 18.0, 1.0));
        foods.add(new FoodItem("Chapati (Wheat Roti)", 264, 9.0, 52.0, 2.0, 6.5));
        foods.add(new FoodItem("Masala Dosa", 165, 3.5, 28.0, 4.5, 2.0));
        foods.add(new FoodItem("Chicken Biryani", 180, 12.0, 22.0, 6.0, 1.5));
        foods.add(new FoodItem("Idli (2 pieces)", 116, 4.0, 25.0, 0.2, 1.0));
        foods.add(new FoodItem("Palak Paneer", 150, 7.0, 5.0, 12.0, 2.5));
        foods.add(new FoodItem("Aloo Gobi", 110, 2.5, 14.0, 5.0, 3.0));
        foods.add(new FoodItem("Chole Masala", 160, 6.0, 22.0, 6.0, 7.0));
        foods.add(new FoodItem("Samosa (1 piece)", 260, 3.5, 24.0, 17.0, 1.0));
        foods.add(new FoodItem("Egg (Boiled)", 155, 13.0, 1.1, 11.0, 0.0));
        foods.add(new FoodItem("Egg (Fried)", 196, 14.0, 0.8, 15.0, 0.0));
        foods.add(new FoodItem("Egg Bhurji", 170, 12.0, 2.0, 13.0, 0.5));
        foods.add(new FoodItem("Omelette", 154, 11.0, 0.6, 12.0, 0.0));
        foods.add(new FoodItem("Milk (Whole)", 61, 3.2, 4.8, 3.3, 0.0));
        foods.add(new FoodItem("Apple", 52, 0.3, 14.0, 0.2, 2.4));
        foods.add(new FoodItem("Rice (Steamed)", 130, 2.7, 28.0, 0.3, 0.4));
        foods.add(new FoodItem("Bread (Whole Wheat)", 247, 13.0, 41.0, 3.4, 7.0));
        foods.add(new FoodItem("Oats (Cooked)", 71, 2.5, 12.0, 1.4, 1.7));
        foods.add(new FoodItem("Pancake (Plain)", 227, 6.0, 28.0, 10.0, 0.0));

        dao.insertFoodItems(foods);

        List<ServingUnit> units = new ArrayList<>();
        units.add(new ServingUnit(1, "bowl", 150.0));
        units.add(new ServingUnit(1, "gram", 1.0));
        units.add(new ServingUnit(2, "serving", 200.0));
        units.add(new ServingUnit(2, "gram", 1.0));
        units.add(new ServingUnit(3, "piece", 40.0));
        units.add(new ServingUnit(4, "plate", 250.0));
        units.add(new ServingUnit(5, "plate", 300.0));
        units.add(new ServingUnit(6, "plate", 150.0));
        units.add(new ServingUnit(7, "bowl", 150.0));
        units.add(new ServingUnit(8, "bowl", 150.0));
        units.add(new ServingUnit(9, "bowl", 150.0));
        units.add(new ServingUnit(10, "piece", 50.0));
        units.add(new ServingUnit(11, "egg", 50.0));
        units.add(new ServingUnit(12, "egg", 50.0));
        units.add(new ServingUnit(13, "plate", 150.0));
        units.add(new ServingUnit(14, "plate", 100.0));
        units.add(new ServingUnit(15, "glass", 250.0));
        units.add(new ServingUnit(15, "ml", 1.0));
        units.add(new ServingUnit(16, "medium", 182.0));
        units.add(new ServingUnit(17, "cup", 195.0));
        units.add(new ServingUnit(17, "gram", 1.0));
        units.add(new ServingUnit(18, "slice", 28.0));
        units.add(new ServingUnit(18, "gram", 1.0));
        units.add(new ServingUnit(19, "cup", 234.0));
        units.add(new ServingUnit(20, "piece", 40.0));
        
        dao.insertServingUnits(units);
        android.util.Log.d("AppDatabase", "Seeding complete. Count: " + foods.size());
    }
}
