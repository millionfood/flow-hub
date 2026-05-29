package com.ajh.flow.service;

import com.ajh.flow.dto.history.HistorySearchCond;
import com.ajh.flow.dto.history.StockHistoryDetailDto;
import com.ajh.flow.dto.history.UserHistoryDetailDto;
import com.ajh.flow.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistoryService {

    private final HistoryRepository historyRepository;

    public Page<UserHistoryDetailDto> getUserHistoryList(HistorySearchCond cond, Pageable pageable) {
        return historyRepository.findAllUserHistory(cond, pageable).map(UserHistoryDetailDto::new);
    }

    public Page<StockHistoryDetailDto> getStockHistoryList(HistorySearchCond cond, Pageable pageable){
        return historyRepository.findAllStockHistory(cond, pageable).map(StockHistoryDetailDto::new);
    }
}
