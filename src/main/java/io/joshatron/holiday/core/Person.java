package io.joshatron.holiday.core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Person {
    private List<GiftIdeaAndStatus> wishList;

    public Person() {
        wishList = new ArrayList<>();
    }

    public void addToWishList(GiftIdea idea) {
        wishList.add(new GiftIdeaAndStatus(idea));
    }

    public List<GiftIdea> getMyWishList() {
        return wishList.stream().map(GiftIdeaAndStatus::getIdea).collect(Collectors.toList());
    }

    public List<GiftIdeaAndStatus> getTheirWishList() {
        return wishList;
    }

    public void removeFromWishList(GiftIdea idea) {
        wishList.remove(getMatchingItem(idea));
    }

    public void claimGiftIdea(Person person, GiftIdea ideaToClaim) {
        GiftIdeaAndStatus matching = getMatchingItem(ideaToClaim);
        matching.setClaimed(true);
    }

    private GiftIdeaAndStatus getMatchingItem(GiftIdea idea) {
        return wishList.stream()
                .filter(g -> g.getIdea().equals(idea))
                .findFirst().get();
    }
}
