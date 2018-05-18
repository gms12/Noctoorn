package Model;

import java.util.ArrayList;
import java.util.Arrays;

public class UserSort {
    private User[] userlist;
    private int length;

    public UserSort(){

    }

    public ArrayList<User> alltimeSort (User[] userList){
        this.userlist = userList;
        length = userList.length-1;
        alltimeQuickSort(0,length);
        return new ArrayList<User>(Arrays.asList(userList));
    }

    public ArrayList<User> monthlySort (User[] userList){
        this.userlist = userList;
        length = userList.length-1;
        monthlyQuickSort(0,length);
        return new ArrayList<User>(Arrays.asList(userList));
    }

    public ArrayList<User> weeklySort (User[] userList){
        this.userlist = userList;
        length = userList.length-1;
        weeklyQuickSort(0,length);
        return new ArrayList<User>(Arrays.asList(userList));
    }

    private void alltimeQuickSort(int low,int high){
        int i = low;
        int j = high;
        int pivot = userlist[low + (high-low) / 2].getPoints();

        while( i <= j){
            while(userlist[i].getPoints() < pivot ){
                i++;
            }
            while (userlist[j].getPoints() > pivot){
                j--;
            }

            if(i <= j){
                swapValues(i,j);
                i++;
                j--;
            }
        }
        if(low < j){
            alltimeQuickSort(low,j);
        }
        if(i < high){
            alltimeQuickSort(i,high);
        }
    }

    private void monthlyQuickSort(int low,int high){
        int i = low;
        int j = high;
        int pivot = userlist[low + (high-low) / 2].getPoints_month();

        while( i <= j){
            while(userlist[i].getPoints_month() < pivot ){
                i++;
            }
            while (userlist[j].getPoints_month() > pivot){
                j--;
            }

            if(i <= j){
                swapValues(i,j);
                i++;
                j--;
            }
        }
        if(low < j){
            monthlyQuickSort(low,j);
        }
        if(i < high){
            monthlyQuickSort(i,high);
        }
    }

    private void weeklyQuickSort(int low,int high){
        int i = low;
        int j = high;
        int pivot = userlist[low + (high-low) / 2].getPoints_week();

        while( i <= j){
            while(userlist[i].getPoints_week() < pivot ){
                i++;
            }
            while (userlist[j].getPoints_week() > pivot){
                j--;
            }

            if(i <= j){
                swapValues(i,j);
                i++;
                j--;
            }
        }
        if(low < j){
            weeklyQuickSort(low,j);
        }
        if(i < high){
            weeklyQuickSort(i,high);
        }
    }

    private void swapValues(int i,int j){
        User temp = userlist[i];
        userlist[i] = userlist [j];
        userlist[j] = temp;
    }
}
