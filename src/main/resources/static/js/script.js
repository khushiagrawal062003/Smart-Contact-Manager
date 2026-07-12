// ==========================================================================
// Smart Contact Manager - Premium Front-end Logic
// ==========================================================================

document.addEventListener("DOMContentLoaded", () => {
    // 1. Initialize tooltips or elements
    console.log("Smart Contact Manager UI Initialized.");
    
    // Auto-hide alert messages after 5 seconds
    const alerts = document.querySelectorAll(".alert-dismissible");
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            if (bsAlert) bsAlert.close();
        }, 5000);
    });
});

// 2. Responsive Sidebar Toggler
const toggleSidebar = () => {
    const sidebar = document.querySelector(".sidebar");
    const content = document.querySelector(".content-wrapper");
    
    if (!sidebar || !content) return;

    if (window.innerWidth > 992) {
        // Desktop Collapse Mode
        if (sidebar.style.transform === "translateX(-260px)") {
            sidebar.style.transform = "translateX(0)";
            content.style.marginLeft = "260px";
        } else {
            sidebar.style.transform = "translateX(-260px)";
            content.style.marginLeft = "0px";
        }
    } else {
        // Mobile Toggle Mode
        sidebar.classList.toggle("active");
    }
};

// 3. Delete Contact Confirmation Dialog (Integration with SweetAlert2 & Fallback)
const deleteContactConfirm = (cId, contactName) => {
    if (typeof Swal !== "undefined") {
        Swal.fire({
            title: `Delete ${contactName}?`,
            text: "You won't be able to revert this contact removal!",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#4f46e5",
            cancelButtonColor: "#ef4444",
            confirmButtonText: "Yes, delete it!",
            background: "#ffffff",
            customClass: {
                popup: "glass-card rounded-3"
            }
        }).then((result) => {
            if (result.isConfirmed) {
                window.location.href = `/user/delete/${cId}`;
            }
        });
    } else {
        if (confirm(`Are you sure you want to delete ${contactName}?`)) {
            window.location.href = `/user/delete/${cId}`;
        }
    }
};

// 4. File Upload Image Previewer
const previewImage = (event, previewElementId) => {
    const input = event.target;
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = (e) => {
            const preview = document.getElementById(previewElementId);
            if (preview) {
                preview.src = e.target.result;
            }
        };
        reader.readAsDataURL(input.files[0]);
    }
};

// 5. Client-Side CSV file type validator
const validateCSVInput = (inputElementId) => {
    const fileInput = document.getElementById(inputElementId);
    if (!fileInput || !fileInput.files.length) return false;

    const file = fileInput.files[0];
    const extension = file.name.split(".").pop().toLowerCase();
    
    if (extension !== "csv") {
        if (typeof Swal !== "undefined") {
            Swal.fire({
                title: "Invalid File Format",
                text: "Please select a valid CSV (.csv) file.",
                icon: "error",
                confirmButtonColor: "#4f46e5"
            });
        } else {
            alert("Please select a valid CSV (.csv) file.");
        }
        fileInput.value = ""; // clear input
        return false;
    }
    return true;
};
