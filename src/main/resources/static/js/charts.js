/**
 * Renders the line chart for personal mood and stress analytics.
 * @param {object} analyticsData - The data fetched from the backend via /analytics/me/data.
 * Expected structure: { labels: [], moods: [], stresses: [] }
 */
function renderMyAnalyticsChart(analyticsData) {
  const ctx = document.getElementById('lineChart');
  if (!ctx) return;

  const data = {
    labels: analyticsData.labels,
    datasets: [
      {
        label: 'Mood',
        data: analyticsData.moods,
        borderColor: '#0d6efd',
        backgroundColor: 'rgba(13, 110, 253, 0.5)',
        tension: 0.2, // Increased tension for a smoother curve
        borderWidth: 2,
        pointRadius: 4,
      },
      {
        label: 'Stress',
        data: analyticsData.stresses,
        borderColor: '#dc3545',
        backgroundColor: 'rgba(220, 53, 69, 0.5)',
        tension: 0.2,
        borderWidth: 2,
        pointRadius: 4,
      },
    ],
  };

  new Chart(ctx, {
    type: 'line',
    data: data,
    options: {
      responsive: true,
      maintainAspectRatio: false, // CRITICAL FIX for chart sizing/fit
      scales: {
        y: {
          beginAtZero: true,
          max: 10, // Max score is 10
        },
      },
      plugins: {
        legend: {
          display: true,
        },
      },
    },
  });
}

// REMOVE the old renderDemoLineChart if it still exists.