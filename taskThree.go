package main

import (
	"fmt"
	"sync"
	"time"
)

type Monk struct {
	Name string
	Qi   int
}

func compareMonks(monk1, monk2 Monk) Monk {
	if monk1.Qi > monk2.Qi {
		return monk1
	} else {
		return monk2
	}
}

func findWinner(monks []Monk, left, right int) Monk {
	if left == right {
		return monks[left]
	}

	mid := (left + right) / 2

	winner1 := findWinner(monks, left, mid)
	winner2 := findWinner(monks, mid+1, right)

	return compareMonks(winner1, winner2)
}

func startNewThread(monks []Monk, left, right int, winner chan Monk) {
	go func() {
		winner <- findWinner(monks, left, right)
	}()
}

func findTournamentWinner(monks []Monk) Monk {
	// Create a channel to store the winner of each round.
	winner := make(chan Monk)

	// Create a waitgroup to track the number of threads that are waiting to finish.
	wg := sync.WaitGroup{}

	// Start a new thread for each round of the tournament.
	for i := 0; i < len(monks)-1; i += 2 {
		wg.Add(1)
		startNewThread(monks, i, i+1, winner)
	}

	// Wait for all the threads to finish or for the timeout to expire.
	select {
	case winner := <-winner:
		return winner
	case <-time.After(100 * time.Millisecond):
		// If the timeout expires, return the winner of the first round.
		return <-winner
	}
}

func main() {
	// Create an array of monks.
	monks := []Monk{
		Monk{Name: "Ivanov", Qi: 100},
		Monk{Name: "Petrov", Qi: 90},
		Monk{Name: "Nechiporchuk", Qi: 80},
	}

	// Find the winner of the tournament.
	winner := findTournamentWinner(monks)

	// Print the winner.
	fmt.Println("The winner is:", winner.Name)
}
