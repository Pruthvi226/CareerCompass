document.addEventListener('DOMContentLoaded', () => {
  initializeNavbarScroll();
  initializeTooltips();
  initializeFormValidation();
});

function initializeNavbarScroll() {
  const navbar = document.querySelector('.cc-navbar');
  if (!navbar) {
    return;
  }

  const updateShadow = () => {
    navbar.classList.toggle('scrolled', window.scrollY > 10);
  };

  updateShadow();
  window.addEventListener('scroll', debounce(updateShadow, 20), { passive: true });
}

function initializeTooltips() {
  if (!window.bootstrap) {
    return;
  }

  document.querySelectorAll('[data-bs-toggle="tooltip"]').forEach((element) => {
    new window.bootstrap.Tooltip(element);
  });
}

function initializeFormValidation() {
  document.querySelectorAll('form').forEach((form) => {
    form.addEventListener('submit', (event) => {
      if (!form.checkValidity()) {
        event.preventDefault();
        event.stopPropagation();
      }

      form.classList.add('was-validated');
    });
  });
}

function debounce(callback, waitMs) {
  let timeoutId;

  return (...args) => {
    window.clearTimeout(timeoutId);
    timeoutId = window.setTimeout(() => callback.apply(null, args), waitMs);
  };
}

window.CareerCompass = {
  renderChart(chartId, config) {
    const canvas = document.getElementById(chartId);
    if (!canvas || !window.Chart) {
      return null;
    }

    return new window.Chart(canvas, config);
  },
};
