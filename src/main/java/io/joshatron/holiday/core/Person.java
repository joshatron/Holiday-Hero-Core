package io.joshatron.holiday.core;

import io.joshatron.holiday.core.exception.PersonOperationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        wishList.remove(findTheirItemInWishList(idea));
    }

    public void claimGiftIdea(Person person, GiftIdea ideaToClaim) {
        GiftIdeaAndStatus matching = findTheirItemInWishList(ideaToClaim);
        if (matching.isClaimed()) {
            throw new PersonOperationException();
        }

        matching.setClaimed(true);
        matching.setClaimedBy(person);
    }

    public GiftIdeaAndStatus findTheirItemInWishList(GiftIdea idea) {
        Optional<GiftIdeaAndStatus> matching = wishList.stream()
                .filter(g -> g.getIdea().equals(idea))
                .findFirst();

        if(!matching.isPresent()) {
            throw new PersonOperationException();
        }

        return matching.get();
    }
}
