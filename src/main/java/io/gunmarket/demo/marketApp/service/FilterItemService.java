package io.gunmarket.demo.marketApp.service;

import io.gunmarket.demo.marketApp.model.FilterItem;
import io.gunmarket.demo.marketApp.model.KeyPath;
import io.gunmarket.demo.marketApp.repo.FilterItemRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilterItemService {

    private final FilterItemRepo filterItemRepo;

    public List<FilterItem> getFiltersByMenuItem(String menuItem) {
        List<FilterItem> filterItems = filterItemRepo.findAllByMenuItem(menuItem);
        filterItems.forEach(this::determineValues);
        filterItems.sort(Comparator.comparing(FilterItem::getRank));
        return filterItems;
    }

    private void determineValues(FilterItem filterItem) {
        KeyPath keyPath = filterItem.getKeyPath();
        List<String> values = filterItemRepo.selectFromDistinct(keyPath.getTargetParam(), keyPath.getTargetEntity());
        FilterItem.FilterType type = filterItem.getType();

        if (type == FilterItem.FilterType.CHECKBOX) {
            filterItem.setValues(values);
        } else if (type == FilterItem.FilterType.RANGE) {
            filterItem.setValues(getMinMax(values));
        } else {
            throw new UnsupportedOperationException("Filter type of " + filterItem.getFilterItemId() + " " + filterItem.getName() + " is wrong");
        }
    }

    private List<String> getMinMax(List<String> values) {
        double min = 99999999999d;
        double max = 0d;
        for (String value : values) {
            double currentValue = Double.parseDouble(value);
            min = Math.min(currentValue, min);
            max = Math.max(currentValue, max);
        }
        return List.of(String.valueOf(min), String.valueOf(max));
    }
}
