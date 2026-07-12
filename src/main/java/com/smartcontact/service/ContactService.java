package com.smartcontact.service;

import com.smartcontact.entity.Contact;
import com.smartcontact.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ContactService {
    Contact saveContact(Contact contact, User user);
    Contact updateContact(Contact contact);
    Contact getContactById(Long cId);
    void deleteContact(Long cId, User user);
    Page<Contact> getContactsByUser(Long userId, Pageable pageable);
    
    // Counts & Statistics
    long countTotalContacts(Long userId);
    long countFavoriteContacts(Long userId);
    List<Contact> getRecentlyAdded(Long userId);
    List<Object[]> getCategoryStats(Long userId);
    
    // Searches
    Page<Contact> searchGlobal(Long userId, String query, Pageable pageable);
    Page<Contact> searchMultiFilter(Long userId, String name, String phone, String email, String work, String category, Boolean favorite, Pageable pageable);
    
    // For CSV Export
    List<Contact> getAllContactsForUser(Long userId);
}
