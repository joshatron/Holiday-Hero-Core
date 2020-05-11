package io.joshatron.holiday.core;

import io.joshatron.holiday.core.exception.PersonOperationException;
import io.joshatron.holiday.core.exception.PersonOperationExceptionReason;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class Person {
    private String id;
    private List<GiftIdeaAndStatus> wishList;
    private List<GiftIdeaAndStatus> proposedList;
    private List<GiftIdeaAndStatus> receivedList;

    public Person() {
        wishList = new ArrayList<>();
        proposedList = new ArrayList<>();
        receivedList = new ArrayList<>();
    }

    public Person(String id) {
        this();
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void addIdeaToWishList(GiftIdea idea) {
        wishList.add(new GiftIdeaAndStatus(idea));
    }

    public List<GiftIdea> getMyWishList() {
        return wishList.stream().map(GiftIdeaAndStatus::getIdea).collect(Collectors.toList());
    }

    public List<GiftIdeaAndStatus> getTheirWishList() {
        return wishList;
    }

    public void removeIdeaFromWishList(GiftIdea idea) {
        if(anyItemClaimedInWishlist()) {
            throw new PersonOperationException(PersonOperationExceptionReason.CLAIMING_STARTED);
        }
        wishList.remove(findTheirItemInWishlist(idea));
    }

    private boolean anyItemClaimedInWishlist() {
        return wishList.stream().anyMatch(GiftIdeaAndStatus::isClaimed);
    }

    public void claimGiftIdeaInWishlist(Person person, GiftIdea ideaToClaim) {
        if(person.equals(this)) {
            throw new PersonOperationException(PersonOperationExceptionReason.CANT_CLAIM_OWN);
        }

        GiftIdeaAndStatus matching = findTheirItemInWishlist(ideaToClaim);
        if (matching.isClaimed()) {
            throw new PersonOperationException(PersonOperationExceptionReason.ALREADY_CLAIMED);
        }

        matching.setClaimedBy(person);
    }

    public GiftIdeaAndStatus findTheirItemInWishlist(GiftIdea idea) {
        Optional<GiftIdeaAndStatus> matching = wishList.stream()
                .filter(g -> g.getIdea().equals(idea))
                .findFirst();

        if(!matching.isPresent()) {
            throw new PersonOperationException(PersonOperationExceptionReason.IDEA_NOT_FOUND);
        }

        return matching.get();
    }

    public void removeClaimFromWishlist(GiftIdea idea) {
        findTheirItemInWishlist(idea).removeClaim();
    }

    public boolean wishlistContainsIdea(GiftIdea idea) {
        return wishList.stream().anyMatch(i -> i.getIdea().equals(idea));
    }

    public void rolloverWishlist() {
        proposedList = wishList.stream().filter(GiftIdeaAndStatus::isClaimed).collect(Collectors.toList());
        wishList = wishList.stream().filter(idea -> !idea.isClaimed()).collect(Collectors.toList());
    }

    public boolean proposedListContainsIdea(GiftIdea idea) {
        return proposedList.stream().anyMatch(i -> i.getIdea().equals(idea));
    }

    public List<GiftIdeaAndStatus> getMyProposedList() {
        return proposedList;
    }

    public void acceptIdea(GiftIdea idea) {
        GiftIdeaAndStatus item = proposedList.stream().filter(i -> i.getIdea().equals(idea)).findFirst().get();
        receivedList.add(item);
        proposedList = proposedList.stream().filter(i -> !i.getIdea().equals(idea)).collect(Collectors.toList());
    }

    public boolean receivedListContainsIdea(GiftIdea idea) {
        return receivedList.stream().anyMatch(i -> i.getIdea().equals(idea));
    }

    public void denyIdea(GiftIdea idea) {
        GiftIdeaAndStatus item = proposedList.stream().filter(i -> i.getIdea().equals(idea)).findFirst().get();
        wishList.add(new GiftIdeaAndStatus(item.getIdea()));
        proposedList = proposedList.stream().filter(i -> !i.getIdea().equals(idea)).collect(Collectors.toList());
    }
}
