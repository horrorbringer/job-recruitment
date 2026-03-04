document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
});

// Create page loader if not exists
if (!document.getElementById('page-loader')) {
    const loader = document.createElement('div');
    loader.id = 'page-loader';
    loader.innerHTML = '<div class="spinner-premium"></div>';
    document.body.prepend(loader);
}

// Hide loader on load finish
window.addEventListener('load', function() {
    const loader = document.getElementById('page-loader');
    if (loader) {
        loader.classList.add('hidden');
        setTimeout(() => {
            if (loader.parentNode) loader.parentNode.removeChild(loader);
        }, 400);
    }
});

// Show loader on page unload (navigation)
window.addEventListener('beforeunload', function() {
    let loader = document.getElementById('page-loader');
    if (!loader) {
        loader = document.createElement('div');
        loader.id = 'page-loader';
        loader.innerHTML = '<div class="spinner-premium"></div>';
        document.body.prepend(loader);
    }
    loader.classList.remove('hidden');
});

// Add loading state to form submit buttons
document.addEventListener('submit', function(e) {
    const form = e.target;
    // Disregard if not properly validated
    if (!form.checkValidity()) {
        return;
    }
    
    // Add loading indicator to submit buttons
    const submitBtn = form.querySelector('button[type="submit"], input[type="submit"]');
    if (submitBtn && submitBtn.classList && !submitBtn.classList.contains('is-loading')) {
        // Prevent generic button width shrinking by fixing width
        submitBtn.style.minWidth = submitBtn.offsetWidth + 'px';
        submitBtn.classList.add('is-loading');
        // Disable with tiny delay so submission naturally processes
        setTimeout(() => {
            submitBtn.disabled = true;
        }, 50);
    }
});
