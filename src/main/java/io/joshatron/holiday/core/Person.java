package io.joshatron.holiday.core;

import io.joshatron.holiday.core.exception.PersonOperationException;
import io.joshatron.holiday.core.exception.PersonOperationExceptionReason;

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
        if(anyItemClaimed()) {
            throw new PersonOperationException(PersonOperationExceptionReason.CLAIMING_STARTED);
        }
        wishList.remove(findTheirItemInWishList(idea));
    }

    private boolean anyItemClaimed() {
        return wishList.stream().anyMatch(GiftIdeaAndStatus::isClaimed);
    }

    public void claimGiftIdea(Person person, GiftIdea ideaToClaim) {
        if(person.equals(this)) {
            throw new PersonOperationException(PersonOperationExceptionReason.CANT_CLAIM_OWN);
        }

        GiftIdeaAndStatus matching = findTheirItemInWishList(ideaToClaim);
        if (matching.isClaimed()) {
            throw new PersonOperationException(PersonOperationExceptionReason.ALREADY_CLAIMED);
        }

        matching.setClaimed(true);
        matching.setClaimedBy(person);
    }

    public GiftIdeaAndStatus findTheirItemInWishList(GiftIdea idea) {
        Optional<GiftIdeaAndStatus> matching = wishList.stream()
                .filter(g -> g.getIdea().equals(idea))
                .findFirst();

        if(!matching.isPresent()) {
            throw new PersonOperationException(PersonOperationExceptionReason.IDEA_NOT_FOUND);
        }

        return matching.get();
    }
}
