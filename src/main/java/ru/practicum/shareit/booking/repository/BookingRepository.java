package ru.practicum.shareit.booking.repository;

import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long bookerId, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId,
                                                              LocalDateTime start,
                                                              LocalDateTime end,
                                                              Sort sort);

    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndEndIsAfter(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findAllByItemIdIn(List<Long> itemIds, Sort sort);

    @Query("select b " +
            "from Booking b " +
            "where b.item.id in :itemIds " +
            "and b.start <= :now and b.end >= :now ")
    List<Booking> findAllByItemIdInAndStartBeforeAndEndAfter(@Param("itemIds") List<Long> itemIds,
                                                             @Param("now") LocalDateTime now,
                                                             Sort sort);

    List<Booking> findAllByItemIdInAndEndBefore(List<Long> itemIds, LocalDateTime now, Sort sort);

    List<Booking> findAllByItemIdInAndStartAfter(List<Long> itemIds, LocalDateTime now, Sort sort);

    List<Booking> findAllByItemIdInAndStatus(List<Long> itemIds, BookingStatus status, Sort sort);

    @Query("select b " +
            "from Booking b " +
            "where b.item.owner.id = :ownerId " +
            "and b.status = :status " +
            "and b.end < :now " +
            "order by b.end desc ")
    List<Booking> findLastBookingsByOwner(@Param("ownerId") Long ownerId,
                                          @Param("status") BookingStatus status,
                                          @Param("now") LocalDateTime now);

    @Query("select b " +
            "from Booking b " +
            "where b.item.owner.id = :ownerId " +
            "and b.status = :status " +
            "and b.start > :now " +
            "order by b.start asc ")
    List<Booking> findNextBookingsByOwner(@Param("ownerId") Long ownerId,
                                          @Param("status") BookingStatus status,
                                          @Param("now") LocalDateTime now);

    @Query("select b " +
            "from Booking b " +
            "where b.item.id = :itemId " +
            "and b.status = :status " +
            "and b.end < :now " +
            "order by b.end desc ")
    List<Booking> findLastBookingsByItem(@Param("itemId") Long itemId,
                                         @Param("status") BookingStatus status,
                                         @Param("now") LocalDateTime now);

    @Query("select b " +
            "from Booking b " +
            "where b.item.id = :itemId " +
            "and b.status = :status " +
            "and b.start > :now " +
            "order by b.start asc ")
    List<Booking> findNextBookingsByItem(@Param("itemId") Long itemId,
                                         @Param("status") BookingStatus status,
                                         @Param("now") LocalDateTime now);

    @Query("select b " +
            "from Booking b " +
            "where b.item.id = :itemId " +
            "and b.booker.id = :bookerId " +
            "and b.status = :status " +
            "and b.end < :end ")
    List<Booking> findByItemIdAndBookerIdAndStatusAndEndIsBefore(Long itemId, Long bookerId, BookingStatus status,
                                                                 LocalDateTime end);
}