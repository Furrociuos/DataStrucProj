# DataStrucProj
A simple household management program
## TODOs:
### ARRAY
  - [x] Store the names of the family members in an array
  - [x] Use an array to hold different categories of expenses
### LINKED LIST
  - [x] Build a TaskHistory linked list that logs already completed chores in chronological order (append on completion)
  - [x] Build an ExpenseHistory linked list for recorded transactions, supporting insertion at the tail and traversal for statements
### QUEUE
  - [x] Use a Queue to hold pending chores in the order they should be completed
  - [x] Implement enqueue/dequeue for chore assignment round-robin among family members
### BST
  - [x] Build a BST of expenses keyed by amount or due dates
  - [x] Build a BST of chores keyed by deadline for in-order traversal
  - [x] Implement search, insert, delete, and in-order traversal methods
### HEAP
  - [x] Implement a min-heap of chores ordered by urgency
  - [x] Implement a heap of unpaid bills ordered by due date
  - [x] Support insert, extractMin, and peek operations
### Hash Table
  - [x] Map memberID -> Member Object for O(1) lookup of family members and their assigned chores
  - [x] Map taskID -> Task object and expenseID -> Expense object for quick retrieval/updates
  - [x] Use a hash table to group expenses by categories
### Graph
  - [x] Model chore dependencies as a graph (e.g., vacuum before declutter) and support topological sort to determine valid task order
  - [x] Implement traversal (BFS/DFS) to detect dependency cycles
### Integration
  - [ ] Create a service class that implements all the data structure classes
  - [x] Create a user interface

## NOTE
AI was used to generate TODOs for ease of assignment, creation of a framework, and to speed up development
