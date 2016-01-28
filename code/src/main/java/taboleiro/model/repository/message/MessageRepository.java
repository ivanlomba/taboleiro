package taboleiro.model.repository.message;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import taboleiro.model.domain.message.Message;

public interface MessageRepository extends CrudRepository<Message, Long> {

    Message findMessageByMessageId(Long messageId);

    @Query("SELECT msg from message msg WHERE msg.addressee.userId = :addressee and msg.copy.userId = :copy" +
            " ORDER BY msg.messageDate DESC")
    Page<Message> findMessageByAddresseeAndCopyOrderByMessageDateDesc(Pageable page, @Param("addressee")Long addressee,
                                                                      @Param("copy")Long copy);

    @Query("SELECT msg from message msg WHERE msg.sender.userId = :sender and msg.copy.userId = :copy" +
            " ORDER BY msg.messageDate DESC")
    Page<Message> findMessageBySenderAndCopyOrderByMessageDateDesc(Pageable page, @Param("sender")Long sender, @Param("copy")Long copy);

    @Query("SELECT COUNT(msg) from message msg WHERE msg.addressee.userId = :addressee and msg.copy.userId = :addressee and" +
            " msg.viewed = false")
    Integer countNotViewedMail(@Param("addressee")Long addressee);

}
