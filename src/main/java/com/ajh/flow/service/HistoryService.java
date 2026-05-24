package com.ajh.flow.service;

import com.ajh.flow.dto.history.StockHistoryDetailDto;
import com.ajh.flow.dto.history.UserHistoryDetailDto;
import com.ajh.flow.dto.history.HistorySearchCond;
import com.ajh.flow.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistoryService {

    private final HistoryRepository historyRepository;

    public List<UserHistoryDetailDto> getUserHistoryList(HistorySearchCond cond){
        return historyRepository.findAllUserHistory(cond).stream()
                .map(UserHistoryDetailDto::new).collect(Collectors.toList());
    }

    public List<StockHistoryDetailDto> getStockHistoryList(HistorySearchCond cond){
        return historyRepository.findAllStockHistory(cond).stream()
                .map(StockHistoryDetailDto::new).collect(Collectors.toList());
    }
}
