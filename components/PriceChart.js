import React from 'react';
import { View, Text, StyleSheet, Dimensions } from 'react-native';

const { width } = Dimensions.get('window');
const CHART_WIDTH = width * 0.9;
const CHART_HEIGHT = 220;

// Hexagon pattern background component
const HexagonPattern = () => {
  // Create a grid of hexagons
  const hexagons = [];
  const hexSize = 15;
  const rows = Math.ceil(CHART_HEIGHT / (hexSize * 1.5));
  const cols = Math.ceil(CHART_WIDTH / (hexSize * 1.732));
  
  for (let r = 0; r < rows; r++) {
    for (let c = 0; c < cols; c++) {
      const offsetX = c * hexSize * 1.732;
      const offsetY = r * hexSize * 1.5;
      // Offset every other row
      const adjustedX = r % 2 === 0 ? offsetX : offsetX + hexSize * 0.866;
      
      hexagons.push(
        <View 
          key={`hex-${r}-${c}`}
          style={[
            styles.hexagon,
            {
              left: adjustedX,
              top: offsetY,
            }
          ]}
        />
      );
    }
  }
  
  return (
    <View style={styles.hexagonContainer}>
      {hexagons}
    </View>
  );
};

export default function PriceChart() {
  // Mock data for the chart
  const priceData = [
    1910.25, 1915.50, 1920.75, 1918.30, 1922.45, 
    1925.10, 1923.80, 1928.65, 1930.20, 1927.90,
    1932.40, 1935.75, 1933.25, 1936.80, 1940.15
  ];
  
  // Find min and max for scaling
  const minPrice = Math.min(...priceData);
  const maxPrice = Math.max(...priceData);
  const priceRange = maxPrice - minPrice;
  
  // Calculate points for the chart
  const points = priceData.map((price, index) => {
    const x = (index / (priceData.length - 1)) * CHART_WIDTH;
    const y = CHART_HEIGHT - ((price - minPrice) / priceRange) * CHART_HEIGHT;
    return { x, y };
  });

  return (
    <View style={styles.container}>
      <View style={styles.headerRow}>
        <Text style={styles.title}>Gold Price Chart</Text>
        <View style={styles.chartIndicator}>
          <Text style={styles.chartText}>15 DAYS</Text>
        </View>
      </View>
      
      <View style={styles.chartContainer}>
        {/* Background pattern */}
        <HexagonPattern />
        
        {/* Y-axis labels */}
        <View style={styles.yAxis}>
          <Text style={styles.axisLabel}>${maxPrice.toFixed(2)}</Text>
          <Text style={styles.axisLabel}>${((maxPrice + minPrice) / 2).toFixed(2)}</Text>
          <Text style={styles.axisLabel}>${minPrice.toFixed(2)}</Text>
        </View>
        
        {/* Chart */}
        <View style={styles.chart}>
          {/* Draw the line chart */}
          <View style={styles.chartLine}>
            {points.map((point, index) => (
              <View 
                key={index} 
                style={[
                  styles.dataPoint,
                  { left: point.x, top: point.y }
                ]}
              />
            ))}
          </View>
          
          {/* Connect the points with lines */}
          {points.map((point, index) => {
            if (index === 0) return null;
            const prevPoint = points[index - 1];
            
            // Calculate line length and angle
            const dx = point.x - prevPoint.x;
            const dy = point.y - prevPoint.y;
            const length = Math.sqrt(dx * dx + dy * dy);
            const angle = Math.atan2(dy, dx) * 180 / Math.PI;
            
            return (
              <View
                key={`line-${index}`}
                style={[
                  styles.line,
                  {
                    width: length,
                    left: prevPoint.x,
                    top: prevPoint.y,
                    transform: [{ rotate: `${angle}deg` }],
                    transformOrigin: 'left',
                  }
                ]}
              />
            );
          })}
        </View>
      </View>
      
      {/* Time period selector (just for UI, not functional) */}
      <View style={styles.periodSelector}>
        <View style={[styles.periodButton, styles.selectedPeriodButton]}>
          <Text style={styles.selectedPeriodText}>15D</Text>
        </View>
        <View style={styles.periodButton}>
          <Text style={styles.periodText}>1M</Text>
        </View>
        <View style={styles.periodButton}>
          <Text style={styles.periodText}>3M</Text>
        </View>
        <View style={styles.periodButton}>
          <Text style={styles.periodText}>6M</Text>
        </View>
        <View style={styles.periodButton}>
          <Text style={styles.periodText}>1Y</Text>
        </View>
        <View style={styles.periodButton}>
          <Text style={styles.periodText}>ALL</Text>
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: '#FFFFFF',
    padding: 20,
    borderRadius: 10,
    marginVertical: 15,
    width: '90%',
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
    borderWidth: 1,
    borderColor: '#EEEEEE',
  },
  headerRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    width: '100%',
    marginBottom: 15,
  },
  title: {
    color: '#000000',
    fontSize: 18,
    fontWeight: 'bold',
  },
  chartIndicator: {
    backgroundColor: '#0066FF',
    paddingHorizontal: 10,
    paddingVertical: 5,
    borderRadius: 5,
  },
  chartText: {
    color: '#FFFFFF',
    fontWeight: 'bold',
    fontSize: 12,
  },
  chartContainer: {
    flexDirection: 'row',
    height: CHART_HEIGHT,
    width: CHART_WIDTH,
    position: 'relative',
    backgroundColor: '#F9F9F9',
    borderRadius: 8,
    overflow: 'hidden',
  },
  hexagonContainer: {
    position: 'absolute',
    width: '100%',
    height: '100%',
  },
  hexagon: {
    position: 'absolute',
    width: 12,
    height: 7,
    borderLeftWidth: 6,
    borderRightWidth: 6,
    borderTopWidth: 3.5,
    borderBottomWidth: 3.5,
    borderLeftColor: 'transparent',
    borderRightColor: 'transparent',
    borderTopColor: '#EEEEEE',
    borderBottomColor: '#EEEEEE',
  },
  yAxis: {
    width: 50,
    height: CHART_HEIGHT,
    justifyContent: 'space-between',
    paddingVertical: 10,
    paddingLeft: 5,
  },
  axisLabel: {
    color: '#555555',
    fontSize: 10,
  },
  chart: {
    flex: 1,
    height: CHART_HEIGHT,
    position: 'relative',
    borderLeftWidth: 1,
    borderBottomWidth: 1,
    borderColor: '#DDDDDD',
  },
  chartLine: {
    position: 'absolute',
    width: '100%',
    height: '100%',
  },
  dataPoint: {
    position: 'absolute',
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: '#0066FF',
    marginLeft: -4,
    marginTop: -4,
    borderWidth: 1,
    borderColor: '#FFFFFF',
  },
  line: {
    position: 'absolute',
    height: 2,
    backgroundColor: '#0066FF',
  },
  periodSelector: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    width: '100%',
    marginTop: 15,
  },
  periodButton: {
    paddingHorizontal: 10,
    paddingVertical: 5,
    borderRadius: 5,
  },
  selectedPeriodButton: {
    backgroundColor: '#0066FF',
  },
  periodText: {
    color: '#555555',
    fontSize: 12,
  },
  selectedPeriodText: {
    color: '#FFFFFF',
    fontWeight: 'bold',
    fontSize: 12,
  },
});
