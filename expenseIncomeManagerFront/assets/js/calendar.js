let calendar;

function loadTransactionsFromBackend() {
    fetch(`${API_BASE_URL}/movements?userId=${currentUser.id}`)
        .then(res => res.json())
        .then(data => {
            if(calendar) {
                calendar.removeAllEvents();
                calendar.addEventSource(data);
            }
        })
        .catch(err => console.error('Error cargando movimientos:', err));
}

document.addEventListener('DOMContentLoaded', function() {
    
    const calendarElement = document.getElementById('calendar');
    if(!calendarElement) return;

    calendar = new FullCalendar.Calendar(calendarElement, {
        initialView: 'dayGridMonth',
        locale: 'es',
        height: '100%', 
        expandRows: true,               
        headerToolbar: false,           
        showNonCurrentDates: false,     
        fixedWeekCount: false,          
        firstDay: 1,                    
        events: [],
        
        eventDidMount: function(info) {
            const isIncome = info.event.extendedProps.type === 'INCOME';
            
            const today = new Date();
            today.setHours(0,0,0,0);
            
            const eventDate = new Date(info.event.start);
            eventDate.setHours(0,0,0,0);

            if (eventDate > today) {
                info.el.style.backgroundColor = isIncome ? 'rgba(42, 157, 143, 0.2)' : 'rgba(230, 57, 70, 0.2)';
                info.el.style.borderColor = isIncome ? '#2a9d8f' : '#e63946';
                info.el.style.borderStyle = 'dashed'; 
                info.el.style.borderWidth = '2px';
                info.el.style.color = '#1f2937'; 
            } else {
                info.el.style.backgroundColor = isIncome ? '#2a9d8f' : '#e63946';
                info.el.style.borderColor = isIncome ? '#2a9d8f' : '#e63946';
                info.el.style.color = '#ffffff';
            }
        },
        
        datesSet: function(info) {
            const titleEl = document.getElementById('customCalendarTitle');
            if(titleEl) titleEl.innerText = info.view.title;
        },

        dateClick: (info) => {
            if (typeof prepareModalForCreation === 'function') {
                prepareModalForCreation(info.dateStr);
                new bootstrap.Modal(document.getElementById('transactionModal')).show();
            }
        },
        
        eventClick: (info) => {
            if (info.event.id && info.event.id.startsWith('SCH-')) {
                showNotification('Las transferencias programadas no se pueden editar manualmente.', 'info');
                return;
            }

            if (['admin', 'superadmin', 'basic'].includes(currentUser.role)) {
                if (typeof prepareModalForEdition === 'function') {
                    prepareModalForEdition(info.event);
                    new bootstrap.Modal(document.getElementById('transactionModal')).show();
                }
            }
        }
    });
    
    calendar.render();

    const btnPrev = document.getElementById('btnPrevMonth');
    const btnNext = document.getElementById('btnNextMonth');
    const btnToday = document.getElementById('btnToday');

    if(btnPrev) btnPrev.addEventListener('click', () => calendar.prev());
    if(btnNext) btnNext.addEventListener('click', () => calendar.next());
    if(btnToday) btnToday.addEventListener('click', () => calendar.today());

    refreshDashboard(); 
});