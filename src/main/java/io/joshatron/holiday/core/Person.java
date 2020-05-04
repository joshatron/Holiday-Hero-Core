package io.joshatron.holiday.core;

import java.util.ArrayList;
import java.util.List;

public class Person {
    private List<GiftIdea> wishList;

    public Person() {
        wishList = new ArrayList<>();
    }

    public void addToWishList(GiftIdea idea) {
        wishList.add(idea);
    }

    public List<GiftIdea> getWishList() {
        return wishList;
    }
}
